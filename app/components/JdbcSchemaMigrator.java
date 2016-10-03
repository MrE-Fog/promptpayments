package components;

import play.db.Database;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Makeschift schema migration until we do something proper
 */
class JdbcSchemaMigrator {
    static Connection InitialiseSchema(Database db) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.createStatement().execute("DROP TABLE Company; DROP TABLE Report;");
        }
        catch(Exception e)
        {
            play.Logger.info("Didn't drop tables.");
        }

        try {
            conn = db.getConnection();
            play.Logger.debug("Creating schema...");
            conn.createStatement().execute(
                    "CREATE TABLE Company(CompaniesHouseIdentifier nvarchar(30), Name nvarchar(256));" +
                    "CREATE TABLE Report(Identifier INTEGER, CompaniesHouseIdentifier nvarchar(30), FilingDate date);" +

                    "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Tesco', '120');" +
                    "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Costa', '121');" +
                    "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Eigencode Ltd.', '122');" +

                    "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate) VALUES (1, '120', '2015-02-01');" +
                    "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate) VALUES (2, '120', '2015-08-01');" +
                    "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate) VALUES (3, '120', '2016-02-01');" +
                    "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate) VALUES (1, '121', '2015-07-01');" +
                    "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate) VALUES (2, '121', '2016-01-01');" +
                    "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate) VALUES (1, '122', '2016-05-01');"
            );

            play.Logger.debug("Schema created, rowcount of reports:");
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM Company WHERE CompaniesHouseIdentifier = '120'");
            while (resultSet.next()) {
                play.Logger.debug("" + resultSet.getString(1) + " " + resultSet.getString(2));
            }
        }
        catch(Exception e) {
            play.Logger.error("Cant build schema", e);
        }
        return conn;
    }
}
