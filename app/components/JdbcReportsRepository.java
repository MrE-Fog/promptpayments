package components;

import models.*;
import play.libs.F;
import scala.Option;
import utils.TimeProvider;

import javax.inject.Inject;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
        List<CompanySummary> companySummaries = jdbcCommunicator.ExecuteQuery(
                "SELECT Name, CompaniesHouseIdentifier FROM Company WHERE LOWER(Name) LIKE LOWER(?) ORDER BY Name LIMIT ? OFFSET ?",
                new Object[]{"%" + company.trim() + "%", itemsPerPage, page*itemsPerPage},
                _CompanySummaryMapper);

        Integer total = jdbcCommunicator.ExecuteQuery(
                "SELECT COUNT(*) FROM Company WHERE LOWER(Name) LIKE LOWER(?)",
                new Object[]{"%" + company.trim() + "%"},
                x -> x.getInt(1)).get(0);

        return new PagedList<>(companySummaries, total, page, itemsPerPage);
    }

    @Override
    public Option<CompanyModel> getCompanyByCompaniesHouseIdentifier(String identifier, int page, int itemsPerPage) {
        Option<CompanySummary> summary = GetCompanySummaryByIdentifier(identifier);
        if (summary.isEmpty()) {
            return Option.empty();
        }

        List<ReportSummary> reports = jdbcCommunicator.ExecuteQuery(
                "SELECT Identifier, FilingDate FROM Report WHERE CompaniesHouseIdentifier = ? ORDER BY FilingDate DESC LIMIT ? OFFSET ?",
                new Object[]{identifier, itemsPerPage, page * itemsPerPage},
                _ReportSummaryMapper);

        Integer total = jdbcCommunicator.ExecuteQuery(
                "SELECT COUNT(*) FROM Report WHERE CompaniesHouseIdentifier = ?",
                new Object[]{identifier},
                x -> x.getInt(1)).get(0);

        PagedList<ReportSummary> pagedReports = new PagedList<>(reports, total, page, itemsPerPage);

        return Option.apply(new CompanyModel(summary.get(), pagedReports));
    }

    @Override
    public Option<ReportModel> getReport(String company, int reportId) {
        List<ReportModel> reportModels = jdbcCommunicator.ExecuteQuery(
                "SELECT Identifier, FilingDate, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, PaymentCodes FROM Report WHERE CompaniesHouseIdentifier = ? AND Identifier = ? LIMIT 1",
                new Object[]{company, reportId},
                _ReportMapper(0));

        if (reportModels.isEmpty()) {
            return Option.empty();
        } else {
            return Option.apply(reportModels.get(0));
        }
    }

    @Override
    public PagedList<CompanySummary> getCompanySummaries(List<String> companiesHouseIdentifiers, int page, int itemsPerPage) {
        if (companiesHouseIdentifiers.isEmpty())
            return new PagedList<>(new ArrayList<>(), 0, 0, itemsPerPage);

        //ewwwwww!
        String parameters = String.join(", ", companiesHouseIdentifiers.stream().map(chi -> "?").collect(Collectors.toList()));

        List<Object> identifiersAndPaging = new ArrayList<>(companiesHouseIdentifiers);
        identifiersAndPaging.add(itemsPerPage);
        identifiersAndPaging.add(page * itemsPerPage);

        List<CompanySummary> companySummaries = jdbcCommunicator.ExecuteQuery(
                String.format("SELECT Name, CompaniesHouseIdentifier FROM Company WHERE CompaniesHouseIdentifier IN (%s) ORDER BY Name LIMIT ? OFFSET ?", parameters),
                identifiersAndPaging.toArray(),
                _CompanySummaryMapper);

        Integer size = jdbcCommunicator.ExecuteQuery(
                String.format("SELECT COUNT(*) FROM Company WHERE CompaniesHouseIdentifier IN (%s)", parameters),
                companiesHouseIdentifiers.toArray(),
                x -> x.getInt(1)).get(0);

        return new PagedList<>(companySummaries, size, page, itemsPerPage);
    }

    @Override
    public int TryFileReport(ReportFilingModel rfm, CompanySummary company, Calendar filingDate) {
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

        return jdbcCommunicator.ExecuteUpdate(
                "INSERT INTO Report (FilingDate, CompaniesHouseIdentifier, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, PaymentCodes) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);",
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
                        rfm.getDisputeResolution(),
                        rfm.isOfferEInvoicing(),
                        rfm.isOfferSupplyChainFinance(),
                        rfm.isRetentionChargesInPolicy(),
                        rfm.isRetentionChargesInPast(),
                        rfm.getPaymentCodes()
                },
                x -> x.getInt(1)
        ).get(0);
    }

    @Override
    public List<F.Tuple<CompanySummary, ReportModel>> ExportData(int months) {
        if (months < 1) {
            return new ArrayList<>();
        }

        Calendar minDate = timeProvider.Now();
        minDate.add(Calendar.MONTH, -1 * months);

        return jdbcCommunicator.ExecuteQuery(
                "SELECT Company.Name, Company.CompaniesHouseIdentifier, Report.Identifier, Report.FilingDate, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, PaymentCodes " +
                "FROM Company INNER JOIN Report ON Company.CompaniesHouseIdentifier = Report.CompaniesHouseIdentifier " +
                "WHERE Report.FilingDate >= ?" +
                "LIMIT 100000",
                new Object[] {new Timestamp(minDate.getTimeInMillis())},
                x -> new F.Tuple<>(
                        _CompanySummaryMapper.map(x),
                        _ReportMapper(2).map(x)));

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
            results -> new ReportSummary(results.getInt(1), results.getCalendar(2));

    private JdbcCommunicator.Mapper<ReportModel> _ReportMapper(int offset) {
        return results -> new ReportModel(
                new ReportSummary(results.getInt(1 + offset), results.getCalendar(2 + offset)),
                results.getBigDecimal(3 + offset),
                results.getBigDecimal(4 + offset),
                results.getBigDecimal(5 + offset),
                results.getBigDecimal(6 + offset),
                results.getBigDecimal(7 + offset),
                results.getCalendar(8 + offset),
                results.getCalendar(9 + offset),
                results.getString(10 + offset),
                results.getString(11 + offset),
                results.getBoolean(12 + offset),
                results.getBoolean(13 + offset),
                results.getBoolean(14 + offset),
                results.getBoolean(15 + offset),
                results.getString(16 + offset));
    }

}