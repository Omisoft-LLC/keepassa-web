package com.omisoft.keepassa.listeners;

import static com.omisoft.keepassa.constants.Constants.DB_NAME;
import static com.omisoft.keepassa.constants.Constants.DB_PASSWORD;
import static com.omisoft.keepassa.constants.Constants.DB_URL;
import static com.omisoft.keepassa.constants.Constants.DB_USER;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

/**
 * Created by nslavov on 11/30/16.
 */
@Slf4j
public class DbMigrationListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    log.info("MIGRATING DB REVISIONS");

    Flyway flyway = new Flyway();
    flyway.setDataSource(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
    flyway.setBaselineOnMigrate(true);
    // flyway.setLocations("db/migrations");
    flyway.setSchemas("public");
    flyway.setSqlMigrationPrefix("V");
    flyway.setSqlMigrationSeparator("_");
    flyway.migrate();
    // TODO patch flyway
    executeManulaMigrations(flyway.getDataSource());
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  }

  private void executeManulaMigrations(DataSource ds) {
//    try {
//      Connection conn = ds.getConnection();
    // conn.prepareStatement("SELECT count(*) from 'public'.schema_version case when
    // add_is_active_column_to_all_tables() FOR UPDATE;");
//    } catch (SQLException e) {
//      log.error("SQL EXCEPTION", e);
//    }
  }
}
