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
        
        // 먼저 샘플 데이터가 있는지 확인하고 없으면 생성
        ensureSampleDataExists(memberId);
        
        String sql = """
            SELECT 
                p.prescription_id,
                dep.name AS department_name,
                doc.name AS doctor_name,
                p.issued_at AS treatment_date,
                COALESCE(pp.status, 'START') as status,
                COALESCE(pp.assigned_pharmacist, p.pharmacy_name) AS pharmacy_name,
                COALESCE(ph.pickup_at, p.completed_date) AS completed_date,
                p.completed,
                CASE 
                    WHEN p.completed = true THEN 0
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
                    // 데이터베이스의 완료 상태 설정
                    prescription.setCompleted(rs.getBoolean("completed"));
                    prescriptions.add(prescription);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prescriptions;
    }
    
    /**
     * 샘플 데이터가 없으면 생성
     */
    private void ensureSampleDataExists(long memberId) {
        String checkSql = "SELECT COUNT(*) FROM prescription p JOIN reception r ON p.reception_id = r.reception_id WHERE r.member_id = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            
            pstmt.setLong(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // 샘플 데이터가 없으면 생성
                    createSampleData(memberId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 샘플 처방전 데이터 생성
     */
    private void createSampleData(long memberId) {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 기존 데이터 삭제
            String deleteSql = """
                DELETE p FROM prescription p 
                JOIN reception r ON p.reception_id = r.reception_id 
                WHERE r.member_id = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, memberId);
                ps.executeUpdate();
            }
            
            // 1. 샘플 접수 데이터 생성 (3개)
            String[] receptionData = {
                "INSERT INTO reception (reservation_id, member_id, doctor_id, reception_no, type, status, consent_notice, consent_at, note_to_doctor, created_at, updated_at) VALUES (NULL, ?, 1, 1001, 'DIRECT', 'COMPLETED', true, NOW(), '감기 증상으로 내원', NOW(), NOW())",
                "INSERT INTO reception (reservation_id, member_id, doctor_id, reception_no, type, status, consent_notice, consent_at, note_to_doctor, created_at, updated_at) VALUES (NULL, ?, 2, 1002, 'DIRECT', 'COMPLETED', true, NOW(), '복통으로 내원', NOW(), NOW())",
                "INSERT INTO reception (reservation_id, member_id, doctor_id, reception_no, type, status, consent_notice, consent_at, note_to_doctor, created_at, updated_at) VALUES (NULL, ?, 3, 1003, 'DIRECT', 'COMPLETED', true, NOW(), '두통으로 내원', NOW(), NOW())"
            };
            
            String[] prescriptionData = {
                "INSERT INTO prescription (reception_id, doctor_id, issued_at, content, created_at, updated_at) VALUES (?, 1, '2025-10-16 10:30:00', '감기약 처방', NOW(), NOW())",
                "INSERT INTO prescription (reception_id, doctor_id, issued_at, content, created_at, updated_at) VALUES (?, 2, '2025-10-15 14:20:00', '소화제 처방', NOW(), NOW())",
                "INSERT INTO prescription (reception_id, doctor_id, issued_at, content, created_at, updated_at) VALUES (?, 3, '2025-10-14 09:15:00', '진통제 처방', NOW(), NOW())"
            };
            
            String[] pharmacyData = {
                "INSERT INTO pharmacy_prescription (pharmacy_id, prescription_id, expected_finish_time, status, assigned_pharmacist, created_at, updated_at) VALUES (NULL, ?, '2025-10-16 11:00:00', 'COMPLETED', '슬닥 약국', NOW(), NOW())",
                "INSERT INTO pharmacy_prescription (pharmacy_id, prescription_id, expected_finish_time, status, assigned_pharmacist, created_at, updated_at) VALUES (NULL, ?, '2025-10-15 15:00:00', 'COMPLETED', '건강 약국', NOW(), NOW())",
                "INSERT INTO pharmacy_prescription (pharmacy_id, prescription_id, expected_finish_time, status, assigned_pharmacist, created_at, updated_at) VALUES (NULL, ?, '2025-10-14 10:00:00', 'START', '행복 약국', NOW(), NOW())"
            };
            
            String[] pickupData = {
                "INSERT INTO pickup_history (pharmacy_prescription_id, pickup_at, verified_by, created_at) VALUES (?, '2025-10-16 11:30:00', '김환자', NOW())",
                "INSERT INTO pickup_history (pharmacy_prescription_id, pickup_at, verified_by, created_at) VALUES (?, '2025-10-15 15:30:00', '김환자', NOW())"
            };
            
            for (int i = 0; i < 3; i++) {
                // 1. 접수 데이터 생성
                Long receptionId = null;
                try (PreparedStatement ps = conn.prepareStatement(receptionData[i], Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, memberId);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            receptionId = rs.getLong(1);
                        }
                    }
                }
                
                // 2. 처방전 데이터 생성
                if (receptionId != null) {
                    Long prescriptionId = null;
                    try (PreparedStatement ps = conn.prepareStatement(prescriptionData[i], Statement.RETURN_GENERATED_KEYS)) {
                        ps.setLong(1, receptionId);
                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                prescriptionId = rs.getLong(1);
                            }
                        }
                    }
                    
                    // 3. 약국 처방전 데이터 생성
                    if (prescriptionId != null) {
                        Long pharmacyPrescriptionId = null;
                        try (PreparedStatement ps = conn.prepareStatement(pharmacyData[i], Statement.RETURN_GENERATED_KEYS)) {
                            ps.setLong(1, prescriptionId);
                            ps.executeUpdate();
                            try (ResultSet rs = ps.getGeneratedKeys()) {
                                if (rs.next()) {
                                    pharmacyPrescriptionId = rs.getLong(1);
                                }
                            }
                        }
                        
                        // 4. 수령 완료 데이터 생성 (처음 2개만)
                        if (pharmacyPrescriptionId != null && i < 2) {
                            try (PreparedStatement ps = conn.prepareStatement(pickupData[i])) {
                                ps.setLong(1, pharmacyPrescriptionId);
                                ps.executeUpdate();
                            }
                        }
                    }
                }
            }
            
            conn.commit();
            System.out.println("샘플 처방전 데이터가 생성되었습니다. memberId: " + memberId);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 접수 데이터를 기반으로 처방전 생성
     */
    public Long createPrescriptionFromReception(Long receptionId, String content) {
        String sql = """
            INSERT INTO prescription 
            (reception_id, department, issue_at, content, created_at, updated_at)
            SELECT 
                r.reception_id,
                dep.name AS department,
                NOW() AS issue_at,
                ? AS content,
                NOW() AS created_at,
                NOW() AS updated_at
            FROM reception r
            JOIN doctor doc ON r.doctor_id = doc.doctor_id
            JOIN department dep ON doc.department_id = dep.department_id
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
            (pharmacy_id, prescription_id, expected_finish_time, status, assigned_pharmacist, created_at, updated_at)
            VALUES (NULL, ?, DATE_ADD(NOW(), INTERVAL 30 MINUTE), 'START', ?, NOW(), NOW())
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, prescriptionId);
            pstmt.setString(2, pharmacyName + " (" + pharmacyAddress + ")");
            
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
    public boolean completePickup(Long pharmacyPrescriptionId, String verifiedBy) {
        String sql = """
            INSERT INTO pickup_history 
            (pharmacy_prescription_id, pickup_at, verified_by, created_at)
            VALUES (?, NOW(), ?, NOW())
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, pharmacyPrescriptionId);
            pstmt.setString(2, verifiedBy);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 처방전 완료 상태 업데이트 (영구 저장)
     */
    public boolean updatePrescriptionCompleted(Long prescriptionId, String pharmacyName, String completedDate) {
        String sql = """
            UPDATE prescription 
            SET completed = true, 
                completed_date = ?, 
                pharmacy_name = ?, 
                updated_at = NOW()
            WHERE prescription_id = ?
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, completedDate);
            pstmt.setString(2, pharmacyName);
            pstmt.setLong(3, prescriptionId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 모든 처방전을 완료 상태로 업데이트 (테스트용)
     */
    public boolean updateAllPrescriptionsCompleted(String pharmacyName, String completedDate) {
        String sql = """
            UPDATE prescription p
            JOIN reception r ON p.reception_id = r.reception_id
            SET p.completed = true, 
                p.completed_date = ?, 
                p.pharmacy_name = ?, 
                p.updated_at = NOW()
            WHERE r.member_id = 31 AND p.completed = false
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, completedDate);
            pstmt.setString(2, pharmacyName);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
