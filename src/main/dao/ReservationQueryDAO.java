// src/main/java/dao/ReservationQueryDao.java
package dao;

import common.util.DBConnectionUtil;
import dto.ReservationDetailDTO;
import dto.ReservationSummaryDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/** DriverManager/Properties 기반 공용 DBConnectionUtil 사용 버전 */
public class ReservationQueryDAO {

  private static LocalDate todayKst(){ return LocalDate.now(ZoneId.of("Asia/Seoul")); }

  /* ===================== SQL ===================== */
  private static final String SQL_COUNT_TODAY =
      "SELECT COUNT(*) " +
          "  FROM reservation r " +
          "  JOIN member m ON m.member_id = r.member_id " +
          " WHERE m.uuid = ? " +
          "   AND DATE(r.appointment_at) = ? " +
          "   AND r.status = 'RESERVED'";

  private static final String SQL_FIND_TODAY =
      "SELECT r.reservation_id, r.reservation_no, " +
          "       DATE_FORMAT(r.appointment_at, '%H:%i') AS time_text, " +
          "       dpt.name AS department_name, doc.name AS doctor_name " +
          "  FROM reservation r " +
          "  JOIN member m   ON m.member_id   = r.member_id " +
          "  JOIN doctor doc ON doc.doctor_id = r.doctor_id " +
          "  JOIN department dpt ON dpt.department_id = doc.department_id " +
          " WHERE m.uuid = ? " +
          "   AND DATE(r.appointment_at) = ? " +
          "   AND r.status = 'RESERVED' " +
          " ORDER BY r.appointment_at ASC";

  private static final String SQL_FIND_DETAIL =
      "SELECT r.reservation_id, r.reservation_no, r.doctor_id, " +
          "       dpt.department_id, dpt.name AS department_name, doc.name AS doctor_name, " +
          "       (SELECT rec.reception_id FROM reception rec " +
          "         WHERE rec.reservation_id = r.reservation_id LIMIT 1) AS reception_id " +
          "  FROM reservation r " +
          "  JOIN member m   ON m.member_id   = r.member_id " +
          "  JOIN doctor doc ON doc.doctor_id = r.doctor_id " +
          "  JOIN department dpt ON dpt.department_id = doc.department_id " +
          " WHERE m.uuid = ? AND r.reservation_id = ?";

  /* ===================== APIs ===================== */

  public int countTodayByMemberUuid(String memberUuid) {
    try (Connection c = DBConnectionUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(SQL_COUNT_TODAY)) {
      ps.setString(1, memberUuid);
      ps.setDate(2, Date.valueOf(todayKst()));
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    } catch (SQLException e) {
      throw new RuntimeException("countTodayByMemberUuid failed", e);
    }
  }

  public List<ReservationSummaryDTO> findTodayByMemberUuid(String memberUuid) {
    List<ReservationSummaryDTO> out = new ArrayList<>();
    try (Connection c = DBConnectionUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(SQL_FIND_TODAY)) {
      ps.setString(1, memberUuid);
      ps.setDate(2, Date.valueOf(todayKst()));
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ReservationSummaryDTO d = new ReservationSummaryDTO();
          d.setReservationId(rs.getLong("reservation_id"));
          d.setReservationNo(rs.getString("reservation_no"));
          d.setTimeText(rs.getString("time_text"));
          d.setDepartmentName(rs.getString("department_name"));
          d.setDoctorName(rs.getString("doctor_name"));
          out.add(d);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("findTodayByMemberUuid failed", e);
    }
    return out;
  }

  public ReservationDetailDTO findDetailForReception(String memberUuid, long reservationId) {
    try (Connection c = DBConnectionUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(SQL_FIND_DETAIL)) {
      ps.setString(1, memberUuid);
      ps.setLong(2, reservationId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;

        ReservationDetailDTO d = new ReservationDetailDTO();
        d.setReservationId(rs.getLong("reservation_id"));
        d.setReservationNo(rs.getString("reservation_no"));
        d.setDoctorId(rs.getLong("doctor_id"));
        d.setDepartmentId(rs.getLong("department_id"));
        d.setDepartmentName(rs.getString("department_name"));
        d.setDoctorName(rs.getString("doctor_name"));

        long recId = rs.getLong("reception_id");
        if (!rs.wasNull()) {
          d.setReceptionId(recId);
          d.setAlreadyReception(true);
        } else {
          d.setAlreadyReception(false);
        }
        return d;
      }
    } catch (SQLException e) {
      throw new RuntimeException("findDetailForReception failed", e);
    }
  }
}
