package dao;

import dto.DispensingStatusDTO;
import common.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 조제 DAO
 */
public class DispensingDAO {

    /**
     * 조제 현황 조회
     */
    public DispensingStatusDTO getDispensingStatus(String dispensingId) {
        String sql = """
            SELECT 
                d.dispensing_id,
                p.pharmacy_name,
                p.address,
                p.phone_number,
                p.latitude,
                p.longitude,
                d.status,
                d.received_at,
                d.dispenser_name,
                d.estimated_completion_time,
                d.completed_at,
                d.prescription_details,
                d.qr_code
            FROM dispensings d
            LEFT JOIN pharmacies p ON d.pharmacy_id = p.pharmacy_id
            WHERE d.dispensing_id = ?
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dispensingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new DispensingStatusDTO(
                        rs.getString("dispensing_id"),
                        rs.getString("pharmacy_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("status"),
                        rs.getTimestamp("received_at") != null ? 
                            rs.getTimestamp("received_at").toLocalDateTime().toLocalTime().toString() : null,
                        rs.getString("dispenser_name"),
                        rs.getTimestamp("estimated_completion_time") != null ? 
                            rs.getTimestamp("estimated_completion_time").toLocalDateTime().toLocalTime().toString() : null,
                        rs.getTimestamp("completed_at") != null ? 
                            rs.getTimestamp("completed_at").toLocalDateTime().toLocalTime().toString() : null,
                        rs.getString("prescription_details"),
                        rs.getString("qr_code")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 임시 더미 데이터 (DB 연결 전까지)
        return createDummyDispensingStatus(dispensingId);
    }

    /**
     * 조제 상태 업데이트
     */
    public boolean updateDispensingStatus(String dispensingId, String status, String dispenserName, 
                                        LocalDateTime estimatedCompletionTime, LocalDateTime completedAt) {
        String sql = """
            UPDATE dispensings 
            SET status = ?, dispenser_name = ?, estimated_completion_time = ?, completed_at = ?
            WHERE dispensing_id = ?
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, dispenserName);
            pstmt.setTimestamp(3, estimatedCompletionTime != null ? Timestamp.valueOf(estimatedCompletionTime) : null);
            pstmt.setTimestamp(4, completedAt != null ? Timestamp.valueOf(completedAt) : null);
            pstmt.setString(5, dispensingId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 수령 완료 처리
     */
    public boolean completeReceipt(String dispensingId) {
        return updateDispensingStatus(dispensingId, "RECEIVED_BY_USER", null, null, LocalDateTime.now());
    }

    /**
     * 임시 더미 데이터 생성
     */
    private DispensingStatusDTO createDummyDispensingStatus(String dispensingId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime estimatedTime = now.plusMinutes(30);
        
        return new DispensingStatusDTO(
            dispensingId,
            "슬닥 약국",
            "서울 강남구 도산대로 174",
            "02-1234-5678",
            37.5665,
            126.9780,
            "DISPENSING", // RECEIVED, DISPENSING, COMPLETED, RECEIVED_BY_USER
            "14:20",
            "윤민지",
            "14:35",
            null,
            "감기약, 해열제, 소화제",
            "QR_CODE_" + dispensingId
        );
    }
}
