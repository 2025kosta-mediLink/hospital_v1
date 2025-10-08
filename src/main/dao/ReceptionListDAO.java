package dao;

import common.DBConnectionUtil;
import dto.ReceptionListDetailDTO;
import dto.ReceptionListItemDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceptionListDAO {

    /**
     * 접수 목록 조회 (상태/기간 필터, 정렬만; 페이징 없음)
     * @param memberId 필수: 본인 접수만 조회
     * @param status   null/""/"ALL" 이면 전체, 그 외 WAITING/DONE/...
     * @param from     시작일(yyyy-MM-dd) >= created_at
     * @param to       종료일(yyyy-MM-dd)  <= created_at   (* 실제 쿼리는 to+1 00:00 미만)
     */
    public List<ReceptionListItemDTO> findList(Long memberId,
                                               String status,
                                               LocalDate from,
                                               LocalDate to) {

        List<ReceptionListItemDTO> list = new ArrayList<>();

        // 기본 SELECT + JOIN
        StringBuilder sql = new StringBuilder(
                "SELECT r.reception_id, r.reception_no, r.type, r.status, r.created_at, " +
                        "       d.doctor_id, d.name AS doctor_name, dep.name AS department_name " +
                        "FROM reception r " +
                        "JOIN doctor d ON r.doctor_id = d.doctor_id " +
                        "JOIN department dep ON d.department_id = dep.department_id " +
                        "WHERE r.member_id = ? "
        );

        // 동적 WHERE
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND r.status = ? ");
        }
        if (from != null) {
            sql.append("AND r.created_at >= ? ");
        }
        if (to != null) {
            // to 날짜의 끝까지 포함하려면: (to + 1일) 00:00:00 미만
            sql.append("AND r.created_at < ? ");
        }

        // 최신순 정렬
        sql.append("ORDER BY r.created_at DESC, r.reception_id DESC");

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1;

            // 1) 필수 파라미터
            ps.setLong(i++, memberId);

            // 2) 상태 조건
            if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                ps.setString(i++, status);
            }

            // 3) from 날짜: 00:00:00
            if (from != null) {
                ps.setTimestamp(i++, Timestamp.valueOf(from.atStartOfDay()));
            }

            // 4) to 날짜: (to + 1) 00:00:00
            if (to != null) {
                ps.setTimestamp(i++, Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
            }

            // 실행 & 매핑
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

                    // created_at 매핑
                    // JSP에서 <fmt:formatDate>를 쓰려면 DTO 타입이 java.util.Date 여야 합니다.
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        dto.setCreatedAt(new java.util.Date(ts.getTime())); // ✅ null 안전
                    }

                    list.add(dto); // ✅ 반드시 리스트에 추가!
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

    // (선택) 취소 로직도 camelCase 별칭 불필요
    public boolean cancelReception(Long receptionId) {
        String sql = "UPDATE reception SET status='CANCELLED', updated_at=NOW() WHERE reception_id=?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, receptionId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
