package dao;

import common.util.DBConnectionUtil;
import dto.ReceptionListDetailDTO;
import dto.ReceptionListItemDTO;
import service.ReceptionListService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 목록/상세 + (신규) 취소 트랜잭션 DAO
 *
 * - cancelReceptionTransactional():
 *   접수의 "존재/소유자/상태/처방발행"을 검증한 후,
 *   reception.status -> CANCELLED, waiting_ticket도 CANCELLED로 정리,
 *   notification 삽입까지 하나의 트랜잭션으로 처리합니다.
 */
public class ReceptionListDAO {

  /* ================= 기존 목록/상세 그대로 ================= */

  public List<ReceptionListItemDTO> findList(Long memberId, String status, LocalDate from, LocalDate to) {
    List<ReceptionListItemDTO> list = new ArrayList<>();

    StringBuilder sql = new StringBuilder(
        "SELECT r.reception_id, r.reception_no, r.type, r.status, r.created_at, " +
            "       d.doctor_id, d.name AS doctor_name, dep.name AS department_name " +
            "FROM reception r " +
            "JOIN doctor d ON r.doctor_id = d.doctor_id " +
            "JOIN department dep ON d.department_id = dep.department_id " +
            "WHERE r.member_id = ? "
    );

    if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
      sql.append("AND r.status = ? ");
    }
    if (from != null) {
      sql.append("AND r.created_at >= ? ");
    }
    if (to != null) {
      sql.append("AND r.created_at < ? ");
    }
    sql.append("ORDER BY r.created_at DESC, r.reception_id DESC");

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

      int i = 1;
      ps.setLong(i++, memberId);

      if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
        ps.setString(i++, status);
      }
      if (from != null) {
        ps.setTimestamp(i++, Timestamp.valueOf(from.atStartOfDay()));
      }
      if (to != null) {
        ps.setTimestamp(i++, Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
      }

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ReceptionListItemDTO dto = new ReceptionListItemDTO();
          dto.setReceptionId(rs.getLong("reception_id"));
          dto.setReceptionNo(rs.getInt("reception_no"));
          dto.setType(rs.getString("type"));
          dto.setStatus(rs.getString("status"));
          dto.setDoctorId(rs.getLong("doctor_id"));
          dto.setDoctorName(rs.getString("doctor_name"));
          dto.setDepartmentName(rs.getString("department_name"));

          Timestamp ts = rs.getTimestamp("created_at");
          if (ts != null) dto.setCreatedAt(new java.util.Date(ts.getTime()));

          list.add(dto);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public ReceptionListDetailDTO getReceptionListDetail(Long receptionId) {
    String sql = """
            SELECT
                r.reception_id              AS receptionId,
                r.reception_no              AS receptionNo,
                r.type                      AS type,
                r.status                    AS status,  
                r.consent_notice            AS consentNotice,
                DATE_FORMAT(r.consent_at,  '%Y-%m-%d %H:%i') AS consentAt,
                r.note_to_doctor            AS noteToDoctor,
                DATE_FORMAT(r.created_at,  '%Y-%m-%d %H:%i') AS createdAt,
                DATE_FORMAT(r.updated_at,  '%Y-%m-%d %H:%i') AS updatedAt,
                doc.name                    AS doctorName,
                d.name                      AS departmentName,
                GROUP_CONCAT(s.name ORDER BY s.name SEPARATOR ', ') AS symptomNames
            FROM reception r
            JOIN doctor doc       ON r.doctor_id = doc.doctor_id
            JOIN department d     ON doc.department_id = d.department_id
            LEFT JOIN reception_symptom rs ON r.reception_id = rs.reception_id
            LEFT JOIN symptom s           ON rs.symptom_id = s.symptom_id
            WHERE r.reception_id = ?
            GROUP BY r.reception_id
        """;

    ReceptionListDetailDTO dto = null;

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, receptionId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          dto = new ReceptionListDetailDTO();
          dto.setReceptionId(rs.getLong("receptionId"));
          dto.setReceptionNo(rs.getInt("receptionNo"));
          dto.setType(rs.getString("type"));
          dto.setStatus(rs.getString("status"));
          dto.setConsentNotice(rs.getBoolean("consentNotice"));
          dto.setConsentAt(rs.getString("consentAt"));
          dto.setNoteToDoctor(rs.getString("noteToDoctor"));
          dto.setCreatedAt(rs.getString("createdAt"));
          dto.setUpdatedAt(rs.getString("updatedAt"));
          dto.setDoctorName(rs.getString("doctorName"));
          dto.setDepartmentName(rs.getString("departmentName"));
          dto.setSymptomNames(rs.getString("symptomNames"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dto;
  }
  /* ====================================================== */

  /**
   * (신규) 접수 취소 트랜잭션
   *
   * 1) 검증
   *   - 존재 여부
   *   - 소유자(member_id == 요청자)
   *   - 상태 == 'WAITING' (다른 상태면 취소 불가)
   *   - 처방 존재 여부 (있으면 취소 불가)
   *
   * 2) 상태 변경
   *   - reception.status = 'CANCELLED'
   *   - waiting_ticket.status = 'CANCELLED' (있다면)
   *
   * 3) 부가 기록
   *   - notification에 알림 남기기(선택)
   *
   * 4) commit / 실패시 rollback
   */
  public ReceptionListService.CancelResult cancelReceptionTransactional(Long receptionId, Long memberId, String reason) {
    // 검증용 SELECT
    final String qSelect = """
            SELECT r.member_id, r.status,
                   EXISTS(SELECT 1 FROM prescription p WHERE p.reception_id = r.reception_id) AS has_prescription
            FROM reception r
            WHERE r.reception_id = ?
        """;

    // reception 상태 변경 (동시성 안전: WHERE에 status 조건 포함)
    final String qUpdateReception = """
            UPDATE reception
               SET status = 'CANCELLED', updated_at = NOW()
             WHERE reception_id = ? AND status = 'WAITING'
        """;

    // waiting_ticket 정리 (있을 때만 영향)
    final String qUpdateTicket = """
            UPDATE waiting_ticket
               SET status = 'CANCELLED', updated_at = NOW()
             WHERE reception_id = ? AND status IN ('WAITING','CALLED')
        """;

    // 알림 기록 (선택)
    final String qInsertNotification = """
            INSERT INTO notification
                   (pharmacy_prescription_id, member_id, title, message, type, read_flag, created_at)
            VALUES (NULL, ?, ?, ?, 'RECEPTION', 0, NOW())
        """;

    try (Connection conn = DBConnectionUtil.getConnection()) {

      // 트랜잭션 시작
      boolean oldAutoCommit = conn.getAutoCommit();
      conn.setAutoCommit(false);

      Long ownerId = null;
      String status = null;
      boolean hasPrescription = false;

      // 1) 검증
      try (PreparedStatement ps = conn.prepareStatement(qSelect)) {
        ps.setLong(1, receptionId);
        try (ResultSet rs = ps.executeQuery()) {
          if (!rs.next()) {
            conn.rollback();
            conn.setAutoCommit(oldAutoCommit);
            return new ReceptionListService.CancelResult(false, false, "존재하지 않는 접수입니다.");
          }
          ownerId = rs.getLong("member_id");
          status = rs.getString("status");
          hasPrescription = rs.getBoolean("has_prescription");
        }
      }

      // 소유자 검증
      if (!ownerId.equals(memberId)) {
        conn.rollback();
        conn.setAutoCommit(true);
        return new ReceptionListService.CancelResult(false, false, "본인의 접수만 취소할 수 있습니다.");
      }
      // 처방 발행 여부
      if (hasPrescription) {
        conn.rollback();
        conn.setAutoCommit(true);
        return new ReceptionListService.CancelResult(false, false, "이미 처방이 발행되어 취소할 수 없습니다.");
      }
      // 상태 검증
      if (!"WAITING".equalsIgnoreCase(status)) {
        conn.rollback();
        conn.setAutoCommit(true);
        return new ReceptionListService.CancelResult(false, false, "현재 상태에서는 취소할 수 없습니다.");
      }

      // 2) reception 상태 변경 (동시성 안전: WHERE status='WAITING')
      int updatedReception;
      try (PreparedStatement ps = conn.prepareStatement(qUpdateReception)) {
        ps.setLong(1, receptionId);
        updatedReception = ps.executeUpdate();
      }
      if (updatedReception == 0) {
        // 다른 스레드가 먼저 상태를 바꾼 경우 등
        conn.rollback();
        conn.setAutoCommit(true);
        return new ReceptionListService.CancelResult(false, true, "이미 처리되었거나 취소할 수 없습니다.");
      }

      // 3) waiting_ticket 정리
      try (PreparedStatement ps = conn.prepareStatement(qUpdateTicket)) {
        ps.setLong(1, receptionId);
        ps.executeUpdate(); // 영향 0이어도 에러 아님
      }

      // 4) 알림 기록 (선택)
      try (PreparedStatement ps = conn.prepareStatement(qInsertNotification)) {
        ps.setLong(1, memberId);
        String title = "접수 취소 완료";
        String msg = "접수(ID: " + receptionId + ")가 취소되었습니다."
            + (reason != null && !reason.isBlank() ? " 사유: " + reason : "");
        ps.setString(2, title);
        ps.setString(3, msg);
        ps.executeUpdate();
      }

      // 모든 단계 성공 → commit
      conn.commit();
      conn.setAutoCommit(true);
      return new ReceptionListService.CancelResult(true, true, null);

    } catch (Exception e) {
      e.printStackTrace();
      // 커넥션 close 시 자동 롤백되더라도, 사용자 메시지는 일관되게 반환
      return new ReceptionListService.CancelResult(false, true, "서버 오류가 발생했습니다.");
    }
  }
}
