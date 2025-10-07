package dao;

import common.DBConnectionUtil;
import dto.ReceptionDetailDTO;

import java.sql.*;

public class ReceptionDAO {
  public Long insertReceptionWithSymptoms(Long memberId,
                                          Long doctorId,
                                          String[] symptomIds,
                                          String noteToDoctor,
                                          boolean consentNotice) {
    String insertReceptionSql = "INSERT INTO reception " +
            "(reservation_id, member_id, doctor_id, reception_no, type, status, " +
            "consent_notice, consent_at, note_to_doctor, created_at, updated_at) " +
            "VALUES (NULL, ?, ?, ?, 'DIRECT', 'WAITING', ?, NOW(), ?, NOW(), NOW())";

    String insertSymptomSql = "INSERT INTO reception_symptom " +
            "(reception_id, symptom_id, created_at) VALUES (?, ?, NOW())";

    Connection conn = null;
    Long receptionId = null;

    try {
      conn = DBConnectionUtil.getConnection();
      conn.setAutoCommit(false); // 트랜잭션 시작

      // 1️⃣ reception 테이블 insert
      try (PreparedStatement ps = conn.prepareStatement(insertReceptionSql, Statement.RETURN_GENERATED_KEYS)) {
        int receptionNo = (int) (Math.random() * 10000);
        ps.setLong(1, memberId);
        ps.setLong(2, doctorId);
        ps.setInt(3, receptionNo);
        ps.setBoolean(4, consentNotice);
        ps.setString(5, noteToDoctor);
        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            receptionId = rs.getLong(1);
          }
        }
      }

      // 2️⃣ 선택된 증상 매핑 저장
      if (symptomIds != null && symptomIds.length > 0 && receptionId != null) {
        try (PreparedStatement ps2 = conn.prepareStatement(insertSymptomSql)) {
          for (String sid : symptomIds) {
            ps2.setLong(1, receptionId);
            ps2.setLong(2, Long.parseLong(sid));
            ps2.addBatch();
          }
          ps2.executeBatch();
        }
      }

      conn.commit(); // 둘 다 성공 시 커밋
    } catch (Exception e) {
      if (conn != null) try { conn.rollback(); } catch (Exception ignored) {}
      e.printStackTrace();
    } finally {
      if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
    }

    return receptionId;
  }

  // ✅ receptionId로 접수 상세 조회
  public ReceptionDetailDTO findReceptionDetail(Long receptionId) {
    String sql = "SELECT r.reception_id, r.reception_no, r.status, " +
            "       d.name AS doctor_name, dep.name AS department_name " +
            "FROM reception r " +
            "JOIN doctor d ON r.doctor_id = d.doctor_id " +
            "JOIN department dep ON d.department_id = dep.department_id " +
            "WHERE r.reception_id = ?";

    ReceptionDetailDTO dto = null;

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, receptionId);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          dto = new ReceptionDetailDTO();
          dto.setReceptionId(rs.getLong("reception_id"));
          dto.setReceptionNo(rs.getInt("reception_no"));
          dto.setStatus(rs.getString("status"));
          dto.setDoctorName(rs.getString("doctor_name"));
          dto.setDepartmentName(rs.getString("department_name"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dto;
  }

}
