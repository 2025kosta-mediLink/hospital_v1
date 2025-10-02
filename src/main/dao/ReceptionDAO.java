package dao;

import common.DBConnectionUtil;

import java.sql.*;

public class ReceptionDAO {

  // 접수 저장 (AUTO_INCREMENT 사용)
  public Long insertReception(Long memberId,
                              Long doctorId,
                              String noteToDoctor,
                              boolean consentNotice) {

    String sql = "INSERT INTO reception " +
        "(reservation_id, member_id, doctor_id, reception_no, type, status, " +
        "consent_notice, consent_at, note_to_doctor, created_at, updated_at) " +
        "VALUES (NULL, ?, ?, ?, 'DIRECT', 'WAITING', ?, NOW(), ?, NOW(), NOW())";

    Long generatedId = null;

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      int receptionNo = (int)(Math.random() * 10000); // 단순 난수, 실제로는 시퀀스/규칙 적용 가능

      ps.setLong(1, memberId);       // 로그인된 사용자 ID
      ps.setLong(2, doctorId);       // 선택된 의사 ID
      ps.setInt(3, receptionNo);     // 접수 번호
      ps.setBoolean(4, consentNotice);
      ps.setString(5, noteToDoctor);

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedId = rs.getLong(1);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return generatedId;
  }

  // 접수-증상 관계 저장
  public void insertReceptionSymptom(Long receptionId, String[] symptomIds) {
    if (symptomIds == null || symptomIds.length == 0) return;

    String sql = "INSERT INTO reception_symptom " +
        "(reception_id, symptom_id, created_at) VALUES (?, ?, NOW())";

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      for (String sid : symptomIds) {
        ps.setLong(1, receptionId);
        ps.setLong(2, Long.parseLong(sid));
        ps.addBatch();
      }
      ps.executeBatch();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
