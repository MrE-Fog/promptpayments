package components;

import models.*;
import play.libs.F;
import scala.Array;
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
                "SELECT TOP 1 Identifier, FilingDate, NumberOne, NumberTwo, NumberThree FROM Report WHERE CompaniesHouseIdentifier = ? AND Identifier = ?",
                new Object[]{company, reportId},
                _ReportMapper);

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
    public int TryFileReport(ReportFilingModel rfm, Calendar filingDate) {
        if (!filingDate.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
            throw new InvalidParameterException("filingDate must be UTC");
        }
        if (getCompanyByCompaniesHouseIdentifier(rfm.getTargetCompanyCompaniesHouseIdentifier(), 0, 0).isEmpty()) {
            return -1;
        }

        return jdbcCommunicator.ExecuteUpdate(
                "INSERT INTO Report (FilingDate, CompaniesHouseIdentifier, NumberOne, NumberTwo, NumberThree) VALUES(?,?,?,?,?);",
                new Object[] {filingDate.getTime(), rfm.getTargetCompanyCompaniesHouseIdentifier(), rfm.getNumberOneAsDecimal(), rfm.getNumberTwoAsDecimal(), rfm.getNumberThreeAsDecimal()},
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
                "SELECT TOP 100000 Company.Name, Company.CompaniesHouseIdentifier, Report.Identifier, Report.FilingDate, Report.NumberOne, Report.NumberTwo, Report.NumberThree " +
                "FROM Company INNER JOIN Report ON Company.CompaniesHouseIdentifier = Report.CompaniesHouseIdentifier " +
                "WHERE Report.FilingDate >= ?",
                new Object[] {new Timestamp(minDate.getTimeInMillis())},
                x -> new F.Tuple<>(new CompanySummary(x.getString(1), x.getString(2)),
                        new ReportModel(new ReportSummary(x.getInt(3), x.getCalendar(4)), x.getBigDecimal(5), x.getBigDecimal(6), x.getBigDecimal(7))));

    }

    private Option<CompanySummary> GetCompanySummaryByIdentifier(String identifier) {
        List<CompanySummary> companySummaries = jdbcCommunicator.ExecuteQuery(
                "SELECT TOP 1 Name, CompaniesHouseIdentifier FROM Company WHERE CompaniesHouseIdentifier = ?",
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

    private JdbcCommunicator.Mapper<ReportModel> _ReportMapper =
            results -> new ReportModel(
                    new ReportSummary(results.getInt(1), results.getCalendar(2)),
                    results.getBigDecimal(3), results.getBigDecimal(4), results.getBigDecimal(5));
}