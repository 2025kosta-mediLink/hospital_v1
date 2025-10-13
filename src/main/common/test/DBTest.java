package common.test;

import common.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBTest {
  public static void main(String[] args) {
    String sql = "SELECT member_id, name, login_id FROM member";

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

      System.out.println("DB 연결 성공!");

      while (rs.next()) {
        Long id = rs.getLong("member_id");
        String name = rs.getString("name");
        String loginId = rs.getString("login_id");

        System.out.printf("id=%d, name=%s, loginId=%s%n", id, name, loginId);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
