package components;

import models.*;
import play.libs.F;
import scala.Option;
import utils.TimeProvider;

import javax.inject.Inject;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Database-backed repository.
 */
final class JdbcReportsRepository implements ReportsRepository {

    private JdbcCommunicator jdbcCommunicator;
    private TimeProvider timeProvider;

    @Inject
    JdbcReportsRepository(JdbcCommunicator jdbcCommunicator, TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        this.jdbcCommunicator = jdbcCommunicator;
        this.jdbcCommunicator.InitialiseSchema();
    }

    @Override
    public PagedList<CompanySummary> searchCompanies(String company, int page, int itemsPerPage) {
        String companySanitised = "%" + company.trim() + "%";
        List<CompanySummary> companySummaries = jdbcCommunicator.ExecuteQuery(
                "SELECT Name, CompaniesHouseIdentifier FROM Company WHERE LOWER(Name) LIKE LOWER(?) OR CompaniesHouseIdentifier LIKE ? ORDER BY Name LIMIT ? OFFSET ?",
                new Object[]{companySanitised, companySanitised, itemsPerPage, page*itemsPerPage},
                _CompanySummaryMapper);

        Integer total = jdbcCommunicator.ExecuteQuery(
                "SELECT COUNT(*) FROM Company WHERE LOWER(Name) LIKE LOWER(?) OR CompaniesHouseIdentifier LIKE ?",
                new Object[]{companySanitised, companySanitised},
                x -> x.getInt(1)).get(0);

        return new PagedList<>(companySummaries, total, page, itemsPerPage);
    }

    @Override
    public PagedList<ReportSummary> getReportSummaries(String companiesHouseIdentifier, int page, int itemsPerPage) {
        List<ReportSummary> reports = jdbcCommunicator.ExecuteQuery(
                "SELECT Identifier, FilingDate, StartDate, EndDate FROM Report WHERE CompaniesHouseIdentifier = ? ORDER BY FilingDate DESC LIMIT ? OFFSET ?",
                new Object[]{companiesHouseIdentifier, itemsPerPage, page * itemsPerPage},
                _ReportSummaryMapper);

        Integer total = jdbcCommunicator.ExecuteQuery(
                "SELECT COUNT(*) FROM Report WHERE CompaniesHouseIdentifier = ?",
                new Object[]{companiesHouseIdentifier},
                x -> x.getInt(1)).get(0);

        return new PagedList<>(reports, total, page, itemsPerPage);
    }

    @Override
    public Option<ReportModel> getReport(String company, int reportId) {
        List<ReportModel> reportModels = jdbcCommunicator.ExecuteQuery(
                "SELECT Identifier, FilingDate, StartDate, EndDate, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, MaximumContractPeriod, PaymentTermsChanged, PaymentTermsChangedComment, PaymentTermsChangedNotified, PaymentTermsChangedNotifiedComment, PaymentTermsComment, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, HasPaymentCodes, PaymentCodes FROM Report WHERE CompaniesHouseIdentifier = ? AND Identifier = ? LIMIT 1",
                new Object[]{company, reportId},
                _ReportMapper(0));

        if (reportModels.isEmpty()) {
            return Option.empty();
        } else {
            return Option.apply(reportModels.get(0));
        }
    }

    @Override
    public ReportSummary tryFileReport(ReportFilingModel rfm, CompanySummary company, Calendar filingDate) {
        if (!filingDate.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
            throw new InvalidParameterException("filingDate must be UTC");
        }
        if (!rfm.getTargetCompanyCompaniesHouseIdentifier().equals(company.CompaniesHouseIdentifier)) {
            throw new IllegalArgumentException(String.format("company (%s) and report filing model (%s) don't match", rfm.getTargetCompanyCompaniesHouseIdentifier(), company.CompaniesHouseIdentifier));
        }


        jdbcCommunicator.ExecuteUpdate(
                "INSERT INTO Company (Name, CompaniesHouseIdentifier) SELECT ?, ? WHERE NOT EXISTS (SELECT CompaniesHouseIdentifier FROM Company WHERE CompaniesHouseIdentifier = ?); ",
                new Object[] {
                        company.Name,
                        company.CompaniesHouseIdentifier,
                        company.CompaniesHouseIdentifier
                },
                x -> x.getInt(1)
        );

        int i = jdbcCommunicator.ExecuteUpdate(
                "INSERT INTO Report (FilingDate, CompaniesHouseIdentifier, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, MaximumContractPeriod, PaymentTermsChanged, PaymentTermsChangedComment, PaymentTermsChangedNotified, PaymentTermsChangedNotifiedComment, PaymentTermsComment, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, HasPaymentCodes, PaymentCodes) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);",
                new Object[] {
                        filingDate.getTime(),
                        rfm.getTargetCompanyCompaniesHouseIdentifier(),
                        rfm.getAverageTimeToPayAsDecimal(),
                        rfm.getPercentInvoicesPaidBeyondAgreedTermsAsDecimal(),
                        rfm.getPercentInvoicesWithin30DaysAsDecimal(),
                        rfm.getPercentInvoicesWithin60DaysAsDecimal(),
                        rfm.getPercentInvoicesBeyond60DaysAsDecimal(),
                        rfm.getStartDate().getTime(),
                        rfm.getEndDate().getTime(),
                        rfm.getPaymentTerms(),
                        rfm.getMaximumContractPeriod(),
                        rfm.isPaymentTermsChanged(),
                        rfm.getPaymentTermsChangedComment(),
                        rfm.isPaymentTermsChangedNotified(),
                        rfm.getPaymentTermsChangedNotifiedComment(),
                        rfm.getPaymentTermsComment(),
                        rfm.getDisputeResolution(),
                        rfm.isOfferEInvoicing(),
                        rfm.isOfferSupplyChainFinance(),
                        rfm.isRetentionChargesInPolicy(),
                        rfm.isRetentionChargesInPast(),
                        rfm.isHasPaymentCodes(),
                        rfm.getPaymentCodes()
                },
                x -> x.getInt(1)
        ).get(0);

        return new ReportSummary(i, filingDate, rfm.getStartDate(), rfm.getEndDate());
    }

    @Override
    public List<F.Tuple<CompanySummary, ReportModel>> exportData(int months) {
        if (months < 1) {
            return new ArrayList<>();
        }

        Calendar minDate = timeProvider.Now();
        minDate.add(Calendar.MONTH, -1 * months);

        return jdbcCommunicator.ExecuteQuery(
                "SELECT Company.Name, Company.CompaniesHouseIdentifier, Report.Identifier, Report.FilingDate, Report.StartDate, Report.EndDate, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, MaximumContractPeriod, PaymentTermsChanged, PaymentTermsChangedComment, PaymentTermsChangedNotified, PaymentTermsChangedNotifiedComment, PaymentTermsComment, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, HasPaymentCodes, PaymentCodes " +
                "FROM Company INNER JOIN Report ON Company.CompaniesHouseIdentifier = Report.CompaniesHouseIdentifier " +
                "WHERE Report.FilingDate >= ?" +
                "LIMIT 100000",
                new Object[] {new Timestamp(minDate.getTimeInMillis())},
                x -> new F.Tuple<>(
                        _CompanySummaryMapper.map(x),
                        _ReportMapper(2).map(x)));

    }


    @Override
    public PagedList<CompanySearchResult> getCompanySearchInfo(PagedList<CompanySummaryWithAddress> companySummaries) {
        if (companySummaries.isEmpty()) {
            return new PagedList<CompanySearchResult>(new ArrayList<CompanySearchResult>(), companySummaries.totalSize(), companySummaries.pageNumber(), companySummaries.pageNumber());
        }
        HashMap<String, CompanySummaryWithAddress> companies = new HashMap<>();
        for (CompanySummaryWithAddress c : companySummaries) companies.put(c.CompaniesHouseIdentifier, c);

        String parameters = "('" + String.join("'), ('", companies.keySet()) + "')";

        List<CompanySearchResult> ts = jdbcCommunicator.ExecuteQuery(
                String.format(
                        "SELECT chis.chi, COALESCE(ReportCounts.ReportCount,0) " +
                        "FROM (SELECT CompaniesHouseIdentifier, COUNT(*) as ReportCount FROM Report GROUP BY CompaniesHouseIdentifier) " +
                        "AS ReportCounts " +
                        "RIGHT JOIN (values %s) as chis(chi) ON chis.chi = CompaniesHouseIdentifier",
                        parameters),
                new Object[]{},
                x -> {
                    System.out.println("companies house identifier: " + x.getString(1));
                    return new CompanySearchResult(companies.get(x.getString(1)), x.getInt(2));
                });

        return new PagedList<>(ts, companySummaries.totalSize(), companySummaries.pageNumber(), companySummaries.pageSize());
    }

    private Option<CompanySummary> GetCompanySummaryByIdentifier(String identifier) {
        List<CompanySummary> companySummaries = jdbcCommunicator.ExecuteQuery(
                "SELECT Name, CompaniesHouseIdentifier FROM Company WHERE CompaniesHouseIdentifier = ? LIMIT 1",
                new String[]{identifier},
                _CompanySummaryMapper);
        if (companySummaries.isEmpty()) {
            return Option.empty();
        } else {
            return Option.apply(companySummaries.get(0));
        }
    }

    private JdbcCommunicator.Mapper<CompanySummary> _CompanySummaryMapper =
           results -> new CompanySummary(results.getString(1), results.getString(2));

    private JdbcCommunicator.Mapper<ReportSummary> _ReportSummaryMapper =
            results -> new ReportSummary(results.getInt(1), results.getCalendar(2), results.getCalendar(3), results.getCalendar(4));

    private JdbcCommunicator.Mapper<ReportModel> _ReportMapper(int offset) {
        return results -> new ReportModel(
                new ReportSummary(results.getInt(1 + offset), results.getCalendar(2 + offset), results.getCalendar(3 + offset), results.getCalendar(4 + offset)),
                results.getBigDecimal(5 + offset),
                results.getBigDecimal(6 + offset),
                results.getBigDecimal(7 + offset),
                results.getBigDecimal(8 + offset),
                results.getBigDecimal(9 + offset),
                results.getCalendar(10+ offset),
                results.getCalendar(11+ offset),
                results.getString(12 + offset),
                results.getString(13 + offset),
                results.getBoolean(14 + offset),
                results.getString(15 + offset),
                results.getBoolean(16 + offset),
                results.getString(17 + offset),
                results.getString(18 + offset),
                results.getString(19 + offset),
                results.getBoolean(20 + offset),
                results.getBoolean(21 + offset),
                results.getBoolean(22 + offset),
                results.getBoolean(23 + offset),
                results.getBoolean(24 + offset),
                results.getString(25 + offset));
    }

}