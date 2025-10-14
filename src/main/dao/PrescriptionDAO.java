package dao;

import dto.PrescriptionListItemDTO;
import common.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 처방전 DAO
 */
public class PrescriptionDAO {

    /**
     * 회원의 처방전 목록을 최신순으로 조회
     */
    public List<PrescriptionListItemDTO> getPrescriptionListByMemberId(long memberId) {
        List<PrescriptionListItemDTO> prescriptions = new ArrayList<>();
        
        String sql = """
            SELECT 
                p.prescription_id,
                d.department_name,
                doc.doctor_name,
                p.treatment_date,
                p.status,
                ph.pharmacy_name,
                p.completed_date,
                CASE 
                    WHEN p.status = 'PENDING' THEN 1
                    ELSE 0
                END as can_select
            FROM prescriptions p
            LEFT JOIN departments d ON p.department_id = d.department_id
            LEFT JOIN doctors doc ON p.doctor_id = doc.doctor_id
            LEFT JOIN pharmacies ph ON p.pharmacy_id = ph.pharmacy_id
            WHERE p.member_id = ?
            ORDER BY p.treatment_date DESC
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PrescriptionListItemDTO prescription = new PrescriptionListItemDTO(
                        rs.getLong("prescription_id"),
                        rs.getString("department_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("treatment_date").toString(),
                        rs.getString("status"),
                        rs.getString("pharmacy_name"),
                        rs.getTimestamp("completed_date") != null ? 
                            rs.getTimestamp("completed_date").toLocalDateTime() : null,
                        rs.getBoolean("can_select")
                    );
                    prescriptions.add(prescription);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 임시 더미 데이터 (DB 연결 전까지)
        if (prescriptions.isEmpty()) {
            prescriptions.add(new PrescriptionListItemDTO(
                1L, "내과", "김의사", "2025-01-15", 
                "PENDING", null, null, true
            ));
            prescriptions.add(new PrescriptionListItemDTO(
                2L, "정형외과", "박의사", "2025-01-14", 
                "PENDING", null, null, true
            ));
            prescriptions.add(new PrescriptionListItemDTO(
                3L, "내과", "이의사", "2025-01-13", 
                "COMPLETED", "슬닥 약국", LocalDateTime.now().minusHours(2), false
            ));
        }

        return prescriptions;
    }

    /**
     * 처방전 상태 업데이트
     */
    public boolean updatePrescriptionStatus(long prescriptionId, String status, String pharmacyName, LocalDateTime completedDate) {
        String sql = """
            UPDATE prescriptions 
            SET status = ?, pharmacy_name = ?, completed_date = ?
            WHERE prescription_id = ?
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, pharmacyName);
            pstmt.setTimestamp(3, completedDate != null ? Timestamp.valueOf(completedDate) : null);
            pstmt.setLong(4, prescriptionId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
