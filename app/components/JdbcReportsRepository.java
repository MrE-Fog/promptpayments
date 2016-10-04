package components;

import models.*;
import play.db.Database;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Database-backed repository. Currently the constructor injects some fake data
 */
final class JdbcReportsRepository implements ReportsRepository {

    private Database db;

    @Inject
    public JdbcReportsRepository(Database db) {
        this.db = db;
        JdbcSchemaMigrator.InitialiseSchema(db);
    }

    @Override
    public List<CompanySummary> searchCompanies(String company) {
        return ExecuteQuery(
                "SELECT Name, CompaniesHouseIdentifier FROM Company WHERE LOWER(Name) LIKE LOWER(?)",
                new String[] { "%"+company+"%"},
                _CompanySummaryMapper);
    }

    @Override
    public CompanyModel getCompanyByCompaniesHouseIdentifier(String identifier) {
        CompanySummary summary = GetCompanySummaryByIdentifier(identifier);

        List<ReportSummary> reports = ExecuteQuery(
                "SELECT Identifier, FilingDate FROM Report WHERE CompaniesHouseIdentifier = ?",
                new String[]{identifier},
                _ReportSummaryMapper);

        return new CompanyModel(summary, reports);
    }

    @Override
    public ReportModel getReport(String company, int reportId) {
        return ExecuteQuery(
                "SELECT TOP 1 Identifier, FilingDate, NumberOne, NumberTwo, NumberThree FROM Report WHERE CompaniesHouseIdentifier = ? AND Identifier = ?",
                new Object[]{company, reportId},
                _ReportMapper).get(0);
    }

    @Override
    public List<CompanySummary> getCompanySummaries(List<String> companiesHouseIdentifiers) {
        if (companiesHouseIdentifiers.isEmpty())
            return new ArrayList<>();

        //ewwwwww!
        String parameters = String.join(", ", companiesHouseIdentifiers.stream().map(chi -> "?").collect(Collectors.toList()));

        return ExecuteQuery(
                String.format("SELECT Name, CompaniesHouseIdentifier FROM Company WHERE CompaniesHouseIdentifier IN (%s)", parameters),
                companiesHouseIdentifiers.toArray(),
                _CompanySummaryMapper);
    }

    @Override
    public int TryFileReport(ReportFilingModel rfm) {
        return ExecuteUpdate(
                "INSERT INTO Report (FilingDate, CompaniesHouseIdentifier, NumberOne, NumberTwo, NumberThree) VALUES(?,?,?,?,?);",
                new Object[] {rfm.FilingDateAsDate(), rfm.TargetCompanyCompaniesHouseIdentifier, rfm.NumberOne, rfm.NumberTwo, rfm.NumberThree},
                x -> x.getInt(1)
        ).get(0);
    }

    private CompanySummary GetCompanySummaryByIdentifier(String identifier) {
        return ExecuteQuery(
                "SELECT TOP 1 Name, CompaniesHouseIdentifier FROM Company WHERE CompaniesHouseIdentifier = ?",
                new String[]{identifier},
                _CompanySummaryMapper).get(0);
    }

    private <T> List<T> ExecuteUpdate(String sql, Object[] parameters, Mapper<T> mapper) {
        return Execute(sql, parameters, mapper, x -> {
            x.executeUpdate();
            return x.getGeneratedKeys();
        });
    }
    private <T> List<T> ExecuteQuery(String sql, Object[] parameters, Mapper<T> mapper) {
        return Execute(sql, parameters, mapper, PreparedStatement::executeQuery);
    }

    private <T> List<T> Execute(String sql, Object[] parameters, Mapper<T> mapper, ResultSetGenerator getResultSet) {
        Connection conn;
        try {
            conn = db.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            ResultSet results = getResultSet.apply(statement);
            List<T> rtn = new ArrayList<>();
            while (results.next()) {
                rtn.add(mapper.map(results));
            }

            conn.close();
            return rtn;
        }
        catch(Exception e) {
            play.Logger.error("Can't execute query", e);
            return new ArrayList<>();
        }
    }

    private interface Mapper<T> {
        T map(ResultSet results) throws SQLException;
    }

    private interface ResultSetGenerator {
        ResultSet apply(PreparedStatement statement) throws SQLException;
    }

    private Mapper<CompanySummary> _CompanySummaryMapper =
            results -> new CompanySummary(results.getString(1), results.getString(2));

    private Mapper<ReportSummary> _ReportSummaryMapper =
            results -> new ReportSummary(results.getInt(1), results.getDate(2));

    private Mapper<ReportModel> _ReportMapper =
            results -> new ReportModel(
                    new ReportSummary(results.getInt(1), results.getDate(2)),
                    results.getBigDecimal(3), results.getBigDecimal(4), results.getBigDecimal(5));
}
