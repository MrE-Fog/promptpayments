package components;

import com.google.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Configuration;
import play.db.Database;
import play.db.Databases;
import play.db.DefaultDatabase;
import play.db.NamedDatabase;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 05/10/2016.
 *
 * Tests for JdbcCommunicator
 */
public class JdbcCommunicatorTest {

    private Database testDb;
    private Flyway flyway;

    @Before
    public void setUp() throws Exception {
        testDb = Databases.inMemory("test", "jdbc:h2:mem:playtest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", new HashMap<>());

        flyway = new Flyway();
        flyway.setDataSource(testDb.getDataSource());
        flyway.setLocations("db/migration");
        flyway.clean();
    }

    @After
    public void tearDown() throws Exception {
        testDb.shutdown();
    }

    @Test
    public void initialiseSchema() throws Exception {
        JdbcCommunicator communicator = new JdbcCommunicator(testDb);

        communicator.InitialiseSchema();
        assertTrue("There should at least one migration, but there arent", flyway.info().all().length > 0);
        assertEquals(String.format("All migrations should be applied, but only %s got applied", flyway.info().applied().length),
                flyway.info().all().length, flyway.info().applied().length);
    }

    @Test
    public void execute() throws Exception {
        JdbcCommunicator communicator = new JdbcCommunicator(testDb);
        communicator.InitialiseSchema();

        List<Integer> result = communicator.ExecuteQuery("SELECT ?", new Object[] {42}, x -> x.getInt(1));

        assertEquals("A result should be returned", 1, result.size());
        assertEquals(String.format("The result should be 42 but is %s",result.get(0)), new Integer(42), result.get(0));
    }

    @Test
    public void execute_ReturnsEmptyResultOnError() {
        JdbcCommunicator communicator = new JdbcCommunicator(testDb);
        communicator.InitialiseSchema();

        List<Integer> result = communicator.ExecuteQuery("SELECT ?", new Object[]{}, x -> x.getInt(1));

        assertEquals("An exception should result in an empty result set", 0, result.size());
    }

}