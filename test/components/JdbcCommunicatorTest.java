package components;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by daniel.rothig on 05/10/2016.
 *
 * Tests for JdbcCommunicator
 */
public class JdbcCommunicatorTest {

    private Database testDb;
    private Flyway flyway;
    private JdbcCommunicator communicator;

    @Before
    public void setUp() throws Exception {
        testDb = Databases.inMemory("test", "jdbc:h2:mem:playtest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", new HashMap<>());

        flyway = new Flyway();
        flyway.setDataSource(testDb.getDataSource());
        flyway.setLocations("db/migration");
        flyway.clean();

        communicator = new JdbcCommunicator(testDb);
        communicator.InitialiseSchema();
    }

    @After
    public void tearDown() throws Exception {
        testDb.shutdown();
    }

    @Test
    public void initialiseSchema() throws Exception {
        assertTrue("There should at least one migration, but there arent", flyway.info().all().length > 0);
        assertEquals(String.format("All migrations should be applied, but only %s got applied", flyway.info().applied().length),
                flyway.info().all().length, flyway.info().applied().length);
    }

    @Test
    public void execute() throws Exception {
        List<Integer> result = communicator.ExecuteQuery("SELECT ?", new Object[] {42}, x -> x.getInt(1));

        assertEquals("A result should be returned", 1, result.size());
        assertEquals(String.format("The result should be 42 but is %s",result.get(0)), new Integer(42), result.get(0));
    }

    @Test
    public void executeUpdate() throws Exception {
        List<Integer> insertedKeys = communicator.ExecuteUpdate("INSERT INTO Report (CompaniesHouseIdentifier, Identifier, FilingDate) VALUES (?,?,?);",
                new Object[]{"120", 999, new GregorianCalendar().getTime()},
                x -> x.getInt(1));

        assertEquals(1, insertedKeys.size());
        assertEquals(new Integer(999), insertedKeys.get(0));
    }

    @Test
    public void execute_ReturnsEmptyResultOnError() {
        List<Integer> result = communicator.ExecuteQuery("SELECT ?", new Object[]{}, x -> x.getInt(1));

        assertEquals("An exception should result in an empty result set", 0, result.size());
    }

    @Test
    public void execute_CanHandleDate() throws Exception {
        Date date = new Date();
        List<Calendar> result = communicator.ExecuteQuery("SELECT ?", new Object[]{date}, x -> x.getCalendar(1));

        assertEquals(date, result.get(0).getTime());


    }
}