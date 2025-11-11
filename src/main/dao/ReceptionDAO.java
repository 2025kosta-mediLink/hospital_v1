package dao;

import common.util.DBConnectionUtil;
import dto.ReceptionDetailDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class ReceptionDAO {

  public Long insertReceptionWithSymptoms(String uuid,
                                          Long doctorId,
                                          String[] symptomIds,
                                          String noteToDoctor,
                                          boolean consentNotice) {
    // uuid → member_id 해석
    final String qSelectMemberIdByUuid = "SELECT member_id FROM member WHERE uuid = ?";

    // [변경] MAX(reception_no) 조회 부분 제거 (랜덤 생성 방식으로 대체)
//    final String qSelectReceptionNo = "SELECT COALESCE(MAX(reception_no), 1000) + 1 AS next_no FROM reception";

    // [변경] reception_no를 VARCHAR로 저장하므로 %s 포맷으로 교체
    final String qInsertReception = "INSERT INTO reception " +
        "(reservation_id, member_id, doctor_id, reception_no, type, status, " +
        " consent_notice, consent_at, note_to_doctor, created_at, updated_at) " +
        "VALUES (NULL, ?, ?, ?, 'DIRECT', 'WAITING', ?, NOW(), ?, NOW(), NOW())";

    final String qInsertSymptom = "INSERT INTO reception_symptom " +
            "(reception_id, symptom_id, created_at) VALUES (?, ?, NOW())";

    Connection conn = null;
    Long receptionId = null;

    try {
      conn = DBConnectionUtil.getConnection();
      conn.setAutoCommit(false); // 트랜잭션 시작

      // 0️⃣ uuid → member_id
      Long memberId = null;
      try (PreparedStatement ps = conn.prepareStatement(qSelectMemberIdByUuid)) {
        ps.setString(1, uuid);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            memberId = rs.getLong("member_id");
          }
        }
      }
      if (memberId == null) {
        throw new IllegalStateException("유효하지 않은 사용자(uuid)입니다.");
      }

      // 1️⃣ [변경] 랜덤 접수코드 생성
      String prefix = "RCN-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
      String receptionCode = null;

      // 최대 20회 재시도 (UNIQUE 충돌 방지)
      for (int i = 0; i < 20; i++) {
        String candidate = prefix + String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));
        try (PreparedStatement ps = conn.prepareStatement(qInsertReception, Statement.RETURN_GENERATED_KEYS)) {
          ps.setLong(1, memberId);
          ps.setLong(2, doctorId);
          ps.setString(3, candidate);     // VARCHAR(20)에 저장
          ps.setBoolean(4, consentNotice);
          ps.setString(5, noteToDoctor);
          ps.executeUpdate();

          try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
              receptionId = rs.getLong(1);
              receptionCode = candidate;
              break;
            }
          }
        } catch (SQLIntegrityConstraintViolationException dup) {
          // 중복이면 재시도
          continue;
        } catch (SQLException ex) {
          if ("23000".equals(ex.getSQLState()) && ex.getErrorCode() == 1062) {
            continue;
          }
          throw ex;
        }
      }

      if (receptionId == null)
        throw new IllegalStateException("접수코드 중복으로 생성 실패");

      // 2️⃣ 선택 증상 매핑
      if (symptomIds != null && symptomIds.length > 0) {
        try (PreparedStatement ps2 = conn.prepareStatement(qInsertSymptom)) {
          for (String sid : symptomIds) {
            ps2.setLong(1, receptionId);
            ps2.setLong(2, Long.parseLong(sid));
            ps2.addBatch();
          }
          ps2.executeBatch();
        }
      }

      conn.commit();
      return receptionId;

    } catch (Exception e) {
      if (conn != null) try { conn.rollback(); } catch (Exception ignored) {}
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
    }
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
          dto.setReceptionNo(rs.getString("reception_no")); // ← DTO 필드 타입 String으로 수정
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
