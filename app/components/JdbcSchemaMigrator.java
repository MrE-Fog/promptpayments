package components;

import org.flywaydb.core.Flyway;
import play.db.Database;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Flyway-based schema migration
 */
class JdbcSchemaMigrator {
    static void InitialiseSchema(Database db) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(db.getDataSource());
        flyway.setLocations("db/migration");
        flyway.migrate();
    }
}
