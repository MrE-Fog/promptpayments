package components;

import models.*;
import play.libs.F;
import scala.Option;
import utils.TimeProvider;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    public List<CompanySummary> searchCompanies(String company) {
        return jdbcCommunicator.ExecuteQuery(
                "SELECT Name, CompaniesHouseIdentifier FROM Company WHERE LOWER(Name) LIKE LOWER(?) ORDER BY Name",
                new String[] { "%"+company.trim()+"%"},
                _CompanySummaryMapper);
    }

    @Override
    public Option<CompanyModel> getCompanyByCompaniesHouseIdentifier(String identifier) {
        Option<CompanySummary> summary = GetCompanySummaryByIdentifier(identifier);
        if (summary.isEmpty()) {
            return Option.empty();
        }

        List<ReportSummary> reports = jdbcCommunicator.ExecuteQuery(
                "SELECT Identifier, FilingDate FROM Report WHERE CompaniesHouseIdentifier = ?",
                new String[]{identifier},
                _ReportSummaryMapper);

        return Option.apply(new CompanyModel(summary.get(), reports));
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
    public List<CompanySummary> getCompanySummaries(List<String> companiesHouseIdentifiers) {
        if (companiesHouseIdentifiers.isEmpty())
            return new ArrayList<>();

        //ewwwwww!
        String parameters = String.join(", ", companiesHouseIdentifiers.stream().map(chi -> "?").collect(Collectors.toList()));

        return jdbcCommunicator.ExecuteQuery(
                String.format("SELECT Name, CompaniesHouseIdentifier FROM Company WHERE CompaniesHouseIdentifier IN (%s)", parameters),
                companiesHouseIdentifiers.toArray(),
                _CompanySummaryMapper);
    }

    @Override
    public int TryFileReport(ReportFilingModel rfm) {
        if (getCompanyByCompaniesHouseIdentifier(rfm.TargetCompanyCompaniesHouseIdentifier).isEmpty()) {
            return -1;
        }

        return jdbcCommunicator.ExecuteUpdate(
                "INSERT INTO Report (FilingDate, CompaniesHouseIdentifier, NumberOne, NumberTwo, NumberThree) VALUES(?,?,?,?,?);",
                new Object[] {rfm.FilingDateAsDate(), rfm.TargetCompanyCompaniesHouseIdentifier, rfm.NumberOne, rfm.NumberTwo, rfm.NumberThree},
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
                "SELECT Company.Name, Company.CompaniesHouseIdentifier, Report.Identifier, Report.FilingDate, Report.NumberOne, Report.NumberTwo, Report.NumberThree " +
                "FROM Company INNER JOIN Report ON Company.CompaniesHouseIdentifier = Report.CompaniesHouseIdentifier " +
                "WHERE Report.FilingDate >= ?",
                new Object[] {minDate.getTime()},
                x -> new F.Tuple<>(new CompanySummary(x.getString(1), x.getString(2)),
                        new ReportModel(new ReportSummary(x.getInt(3), x.getDate(4)), x.getBigDecimal(5), x.getBigDecimal(6), x.getBigDecimal(7))));

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
            results -> new ReportSummary(results.getInt(1), results.getDate(2));

    private JdbcCommunicator.Mapper<ReportModel> _ReportMapper =
            results -> new ReportModel(
                    new ReportSummary(results.getInt(1), results.getDate(2)),
                    results.getBigDecimal(3), results.getBigDecimal(4), results.getBigDecimal(5));
}