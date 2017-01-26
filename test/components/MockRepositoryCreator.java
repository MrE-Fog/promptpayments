package components;

import play.db.Database;
import play.db.Databases;
import play.db.DefaultDatabase;
import utils.TimeProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static play.db.Databases.createFrom;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * In-memory database with fixed fake data for testing purposes
 */
class MockRepositoryCreator {


    private final Database testDb;
    private ReportsRepository repository;


    public MockRepositoryCreator(TimeProvider timeProvider) {
        Map<String, String> config = new HashMap<>();
        config.put("user", System.getenv("JDBC_DATABASE_USERNAME"));

        testDb = Databases.createFrom(
                "play",
                "org.postgresql.Driver",
                System.getenv("JDBC_DATABASE_URL"),
                config);

        try {
            repository = CreateMockReportsRepository(timeProvider);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ReportsRepository getMockRepository() {
        return repository;
    }

    public void shutdown() {
        testDb.shutdown();
    }
    private ReportsRepository CreateMockReportsRepository(TimeProvider timeProvider) throws SQLException {

        JdbcReportsRepository jdbcReportsRepository = new JdbcReportsRepository(new JdbcCommunicator(testDb), timeProvider);
        String removeData =
				"DELETE FROM Report;\n" +
                "DELETE FROM Company;";
                		
		
		String fakeData =
                "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Nicecorp', '120');\n" +
                "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Cookies Ltd.', '121');\n" +
                "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Eigencode Ltd.', '122');\n" +
                "\n" +
                "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate, AverageTimeToPay, PercentInvoicesPaidBeyondAgreedTerms, PercentInvoicesPaidWithin30Days, PercentInvoicesPaidWithin60Days, PercentInvoicesPaidBeyond60Days, StartDate, EndDate, PaymentTerms, MaximumContractPeriod, PaymentTermsChanged, PaymentTermsChangedComment, PaymentTermsChangedNotified, PaymentTermsChangedNotifiedComment, PaymentTermsComment, DisputeResolution, OfferEInvoicing, OfferSupplyChainFinance, RetentionChargesInPolicy, RetentionChargesInPast, HasPaymentCodes, PaymentCodes) VALUES (1, '120', '2010-02-01', 31.00, 10.00, 80, 15, 5, '2016-01-01', '2016-05-31', 'User-specified payment terms', 'User-specified maximum contract length', TRUE, 'User-specified payment terms change', TRUE, 'User-specified notification comment', 'User-specified payment terms comment', 'User-specified dispute resolution', TRUE, TRUE,FALSE,FALSE,TRUE,  'Prompt Payment Code');\n" +
                "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-02-01');\n" +
                "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-08-01');\n" +
                "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2016-02-01');\n" +
                "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2015-07-01');\n" +
                "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2016-01-01');\n" +
                "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('122', '2016-05-01');";
        Connection connection = testDb.getConnection();
        connection.createStatement().execute(removeData);
        connection.createStatement().execute(fakeData);

        connection.close();return jdbcReportsRepository;
    }

}
