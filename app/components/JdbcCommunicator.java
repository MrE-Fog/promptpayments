package components;

import com.google.inject.Inject;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.dbsupport.sybase.ase.SybaseASEDbSupport;
import play.db.Database;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Runs queries and performs flyway-based schema migration, which currently injects some fake data
 */
class JdbcCommunicator {
    private Database db;

    @Inject
    JdbcCommunicator(Database db) {
        this.db = db;
    }

    void InitialiseSchema() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(db.getDataSource());
        flyway.setLocations("db/migration");
        flyway.migrate();
    }

    <T> List<T> ExecuteQuery(String sql, Object[] parameters, Mapper<T> mapper) {
        return Execute(sql, parameters, mapper, PreparedStatement::executeQuery);
    }

    <T> List<T> ExecuteUpdate(String sql, Object[] parameters, JdbcCommunicator.Mapper<T> mapper) {
        return Execute(sql, parameters, mapper, x -> {
            x.executeUpdate();
            return x.getGeneratedKeys();
        });
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
                rtn.add(mapper.map(new Columns(results)));
            }

            conn.close();
            return rtn;
        }
        catch(Exception e) {
            System.out.println("Can't execute query " + sql);
            System.out.println(e);
            return new ArrayList<>();
        }

    }

    interface Mapper<T> {
        T map(Columns results) throws SQLException;
    }

    private interface ResultSetGenerator {
        ResultSet apply(PreparedStatement statement) throws SQLException;
    }

    /**
     * Wrapper for ResultSet (for testing purposes)
     */
    @SuppressWarnings("WeakerAccess")
    public class Columns {
        private ResultSet resultSet;
        Columns(ResultSet resultSet) {this.resultSet = resultSet;}

        public String getString(int index) throws SQLException {return resultSet.getString(index);}
        public Calendar getCalendar(int index) throws SQLException {
            Calendar rtn = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            Timestamp timestamp = resultSet.getTimestamp(index);
            if (timestamp != null) {
                rtn.setTime(timestamp);
            }
            return rtn;
        }
        public int getInt(int index) throws SQLException {return resultSet.getInt(index);}
        public BigDecimal getBigDecimal(int index) throws SQLException {return utils.DecimalConverter.getBigDecimal(resultSet.getBigDecimal(index));}

        public boolean getBoolean(int index) throws SQLException {return resultSet.getBoolean(index);}
    }
}
