package in.zeko;


import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author yashchoukse
 */

public class Database {

  private static String URL = "jdbc:postgresql://bsds-db.caxmchgehtb9.us-west-2.rds.amazonaws.com:5432/bsds_db";

  // creds
  private static String ENV_PROPERTY_DB_USERNAME = "DB_USERNAME";
  private static String ENV_PROPERTY_DB_PASSWORD = "DB_PASSWORD";

  // DB config
  private static String DRIVER_CLASS = "org.postgresql.Driver";
  private static String ENV_PROPERTY_DB_CONN_MIN_IDLE = "DB_CONN_MIN_IDLE";
  private static String ENV_PROPERTY_DB_CONN_MAX_ACTIVE = "DB_CONN_MAX_ACTIVE";
  private static String ENV_PROPERTY_DB_CONN_MAX_PREPARED_STATEMENTS = "DB_CONN_MAX_PREPARED_STATEMENTS";
  private static String ENV_PROPERTY_DB_URL = "DB_URL";

  private static BasicDataSource dataSource = null;

  static {
    dataSource = new BasicDataSource();
    dataSource.setDriverClassName(DRIVER_CLASS);

    dataSource.setUrl(System.getProperty(ENV_PROPERTY_DB_URL));
    dataSource.setUsername(System.getProperty(ENV_PROPERTY_DB_USERNAME));
    dataSource.setPassword(System.getProperty(ENV_PROPERTY_DB_PASSWORD));
    dataSource.setMinIdle(Integer.parseInt(System.getProperty(ENV_PROPERTY_DB_CONN_MIN_IDLE)));
    dataSource.setMaxActive(Integer.parseInt(System.getProperty(ENV_PROPERTY_DB_CONN_MAX_ACTIVE)));
    dataSource.setMaxOpenPreparedStatements(Integer.parseInt(System.getProperty(ENV_PROPERTY_DB_CONN_MAX_PREPARED_STATEMENTS)));
  }


  private Database() {}

  public static Connection getConnection() throws SQLException, ClassNotFoundException {

    return dataSource.getConnection();
  }


}
