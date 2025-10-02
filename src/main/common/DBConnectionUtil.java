package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class DBConnectionUtil {

  private static String url;
  private static String username;
  private static String password;
  private static String driver;

  static {
    try {
      Properties prop = new Properties();
      InputStream input = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties");
      prop.load(input);

      driver = prop.getProperty("db.driver");
      url = prop.getProperty("db.url");
      username = prop.getProperty("db.username");
      password = prop.getProperty("db.password");

      Class.forName(driver);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() {
    try {
      return DriverManager.getConnection(url, username, password);
    } catch (Exception e) {
      throw new RuntimeException("DB 연결 실패", e);
    }
  }
}
