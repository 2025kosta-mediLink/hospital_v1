package dao;

import dto.PrescriptionListItemDTO;
import common.util.DBConnectionUtil;

import java.sql.*;
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
                dep.name AS department_name,
                doc.name AS doctor_name,
                p.issued_at AS treatment_date,
                pp.status,
                pp.pharmacy_name,
                ph.pickup_at AS completed_date,
                CASE 
                    WHEN pp.status IS NULL OR pp.status = 'START' THEN 1
                    ELSE 0
                END as can_select
            FROM prescription p
            JOIN reception r ON p.reception_id = r.reception_id
            JOIN doctor doc ON r.doctor_id = doc.doctor_id
            JOIN department dep ON doc.department_id = dep.department_id
            LEFT JOIN pharmacy_prescription pp ON p.prescription_id = pp.prescription_id
            LEFT JOIN pickup_history ph ON pp.pharmacy_prescription_id = ph.pharmacy_prescription_id
            WHERE r.member_id = ?
            ORDER BY p.issued_at DESC
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

        return prescriptions;
    }
    

    /**
     * 접수 데이터를 기반으로 처방전 생성
     */
    public Long createPrescriptionFromReception(Long receptionId, String content) {
        String sql = """
            INSERT INTO prescription 
            (reception_id, doctor_id, issued_at, content, created_at, updated_at)
            SELECT 
                r.reception_id,
                r.doctor_id,
                NOW() AS issued_at,
                ? AS content,
                NOW() AS created_at,
                NOW() AS updated_at
            FROM reception r
            WHERE r.reception_id = ?
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, content);
            pstmt.setLong(2, receptionId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 처방전을 약국으로 전송 (pharmacy_prescription 테이블에 저장)
     */
    public Long sendPrescriptionToPharmacy(Long prescriptionId, String pharmacyName, String pharmacyAddress) {
        String sql = """
            INSERT INTO pharmacy_prescription 
            (pharmacy_id, prescription_id, pharmacy_name, expected_finish_time, status, assigned_pharmacist, created_at, updated_at)
            VALUES (NULL, ?, ?, DATE_ADD(NOW(), INTERVAL 30 MINUTE), 'START', ?, NOW(), NOW())
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, prescriptionId);
            pstmt.setString(2, pharmacyName);
            pstmt.setString(3, pharmacyName + " (" + pharmacyAddress + ")");
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 조제 상태 업데이트
     */
    public boolean updateDispensingStatus(Long pharmacyPrescriptionId, String status) {
        String sql = """
            UPDATE pharmacy_prescription 
            SET status = ?, updated_at = NOW()
            WHERE pharmacy_prescription_id = ?
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setLong(2, pharmacyPrescriptionId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 수령 완료 처리
     */
    public boolean completePickup(Long pharmacyPrescriptionId, Long memberId, String verifiedBy) {
        String sql = """
            INSERT INTO pickup_history 
            (pharmacy_prescription_id, member_id, pickup_at, status, verified_by, created_at)
            VALUES (?, ?, NOW(), 'COMPLETED', ?, NOW())
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, pharmacyPrescriptionId);
            pstmt.setLong(2, memberId);
            pstmt.setString(3, verifiedBy);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
