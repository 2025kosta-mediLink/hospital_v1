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
 * <p>
 * - cancelReceptionTransactional():
 * 접수의 "존재/소유자/상태/처방발행"을 검증한 후,
 * reception.status -> CANCELLED, waiting_ticket도 CANCELLED로 정리,
 * notification 삽입까지 하나의 트랜잭션으로 처리합니다.
 */
public class ReceptionListDAO {

    /* ================= 기존 목록/상세 그대로 ================= */

    // ⬇️ 추가: uuid 기반 목록 조회 DAO 메서드
//  - r.member_id로 직접 비교하지 않고 member 조인 후 m.uuid로 필터링
    public List<ReceptionListItemDTO> findListByUuid(String uuid, String status, LocalDate from, LocalDate to) {
        List<ReceptionListItemDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT r.reception_id, r.reception_no, r.type, r.status, r.created_at, " +
                        "       d.doctor_id, d.name AS doctor_name, dep.name AS department_name " +
                        "FROM reception r " +
                        "JOIN member m      ON m.member_id = r.member_id " +      // uuid 매핑용 조인
                        "JOIN doctor d      ON r.doctor_id = d.doctor_id " +
                        "JOIN department dep ON d.department_id = dep.department_id " +
                        "WHERE m.uuid = ? "                                         // uuid로 본인 것만
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
            ps.setString(i++, uuid);                        // 1) uuid 바인딩

            if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                ps.setString(i++, status);                    // 2) 상태 필터(선택)
            }
            if (from != null) {
                ps.setTimestamp(i++, Timestamp.valueOf(from.atStartOfDay())); // 3) 시작일 >=
            }
            if (to != null) {
                ps.setTimestamp(i++, Timestamp.valueOf(to.plusDays(1).atStartOfDay())); // 4) 종료일 < (익일 00:00)
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReceptionListItemDTO dto = new ReceptionListItemDTO();
                    dto.setReceptionId(rs.getLong("reception_id"));
                    dto.setReceptionNo(rs.getString("reception_no"));
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
                    dto.setReceptionNo(rs.getString("receptionNo"));
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
     * <p>
     * 1) 검증
     * - 존재 여부
     * - 소유자(member_id == 요청자)
     * - 상태 == 'WAITING' (다른 상태면 취소 불가)
     * - 처방 존재 여부 (있으면 취소 불가)
     * <p>
     * 2) 상태 변경
     * - reception.status = 'CANCELLED'
     * - waiting_ticket.status = 'CANCELLED' (있다면)
     * <p>
     * 3) 부가 기록
     * - notification에 알림 남기기(선택)
     * <p>
     * 4) commit / 실패시 rollback
     */
    public ReceptionListService.CancelResult cancelReceptionTransactional(Long receptionId, String uuid, String reason) {
        // 검증용 SELECT: member 조인 + owner_uuid 조회 추가
        final String qSelect = """
                SELECT r.member_id,
                       r.status,
                       EXISTS(SELECT 1 FROM prescription p WHERE p.reception_id = r.reception_id) AS has_prescription,
                       m.uuid AS owner_uuid
                FROM reception r
                JOIN member m ON m.member_id = r.member_id
                WHERE r.reception_id = ?
                """;

        final String qUpdateReception = """
                UPDATE reception
                   SET status = 'CANCELLED', updated_at = NOW()
                 WHERE reception_id = ? AND status = 'WAITING'
                """;

        final String qUpdateTicket = """
                UPDATE waiting_ticket
                   SET status = 'CANCELLED', updated_at = NOW()
                 WHERE reception_id = ? AND status IN ('WAITING','CALLED')
                """;

        final String qInsertNotification = """
                INSERT INTO notification
                       (pharmacy_prescription_id, member_id, title, message, type, read_flag, created_at)
                VALUES (NULL, ?, ?, ?, 'RECEPTION', 0, NOW())
                """;

        try (Connection conn = DBConnectionUtil.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            Long ownerId = null;
            String status = null;
            boolean hasPrescription = false;
            String ownerUuid = null;

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
                    ownerUuid = rs.getString("owner_uuid");
                }
            }

            // 소유자(uuid) 검증 (memberId -> uuid로 변경)
            if (ownerUuid == null || !ownerUuid.equals(uuid)) {
                conn.rollback();
                conn.setAutoCommit(true);
                return new ReceptionListService.CancelResult(false, false, "본인의 접수만 취소할 수 있습니다.");
            }

            if (hasPrescription) {
                conn.rollback();
                conn.setAutoCommit(true);
                return new ReceptionListService.CancelResult(false, false, "이미 처방이 발행되어 취소할 수 없습니다.");
            }

            if (!"WAITING".equalsIgnoreCase(status)) {
                conn.rollback();
                conn.setAutoCommit(true);
                return new ReceptionListService.CancelResult(false, false, "현재 상태에서는 취소할 수 없습니다.");
            }

            // 2) reception 상태 변경
            int updatedReception;
            try (PreparedStatement ps = conn.prepareStatement(qUpdateReception)) {
                ps.setLong(1, receptionId);
                updatedReception = ps.executeUpdate();
            }
            if (updatedReception == 0) {
                conn.rollback();
                conn.setAutoCommit(true);
                return new ReceptionListService.CancelResult(false, true, "이미 처리되었거나 취소할 수 없습니다.");
            }

            // 3) waiting_ticket 정리
            try (PreparedStatement ps = conn.prepareStatement(qUpdateTicket)) {
                ps.setLong(1, receptionId);
                ps.executeUpdate();
            }

            // 4) 알림 기록 (member_id는 검증 단계에서 얻은 ownerId 사용)
            try (PreparedStatement ps = conn.prepareStatement(qInsertNotification)) {
                ps.setLong(1, ownerId);
                String title = "접수 취소 완료";
                String msg = "접수(ID: " + receptionId + ")가 취소되었습니다."
                        + (reason != null && !reason.isBlank() ? " 사유: " + reason : "");
                ps.setString(2, title);
                ps.setString(3, msg);
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return new ReceptionListService.CancelResult(true, true, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ReceptionListService.CancelResult(false, true, "서버 오류가 발생했습니다.");
        }
    }
}
