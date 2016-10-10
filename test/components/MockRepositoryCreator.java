package components;

import play.db.Database;
import play.db.Databases;
import utils.TimeProvider;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * In-memory database with fixed fake data for testing purposes
 */
class MockRepositoryCreator {
    static ReportsRepository CreateMockReportsRepository(TimeProvider timeProvider) throws SQLException {
        Database testDb = Databases.inMemory("test", "jdbc:h2:mem:playtest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", new HashMap<>());

        JdbcReportsRepository jdbcReportsRepository = new JdbcReportsRepository(new JdbcCommunicator(testDb), timeProvider);

        String fakeData =
"DELETE FROM Report;\n" +
"DELETE FROM Company;\n" +
"INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Nicecorp', '120');\n" +
"INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Cookies Ltd.', '121');\n" +
"INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Eigencode Ltd.', '122');\n" +
"\n" +
"INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, PaymentCodes) VALUES (1, '120', '2010-02-01', 31.00, 10.00, 80, 15, 5, '2016-01-01', '2016-05-31', 'User-specified payment terms', 'User-specified dispute resolution', 1, 1,0,0, 'Prompt Payment Code');\n" +
"INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-02-01');\n" +
"INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-08-01');\n" +
"INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2016-02-01');\n" +
"INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2015-07-01');\n" +
"INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2016-01-01');\n" +
"INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('122', '2016-05-01');";
        testDb.getConnection().createStatement().execute(fakeData);

        return jdbcReportsRepository;
    }
}
