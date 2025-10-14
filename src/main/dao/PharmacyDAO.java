package dao;

import dto.PharmacyListItemDTO;
import common.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 약국 DAO
 */
public class PharmacyDAO {

    /**
     * 위치 기반 약국 검색
     */
    public List<PharmacyListItemDTO> searchNearbyPharmacies(double latitude, double longitude, int radiusMeters) {
        List<PharmacyListItemDTO> pharmacies = new ArrayList<>();
        
        String sql = """
            SELECT 
                pharmacy_id,
                pharmacy_name,
                address,
                phone_number,
                latitude,
                longitude,
                (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * 
                 cos(radians(longitude) - radians(?)) + sin(radians(?)) * 
                 sin(radians(latitude)))) AS distance,
                operating_hours,
                is_open,
                rating,
                status
            FROM pharmacies 
            WHERE (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * 
                   cos(radians(longitude) - radians(?)) + sin(radians(?)) * 
                   sin(radians(latitude)))) <= ?
            ORDER BY distance ASC
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, latitude);
            pstmt.setDouble(2, longitude);
            pstmt.setDouble(3, latitude);
            pstmt.setDouble(4, latitude);
            pstmt.setDouble(5, longitude);
            pstmt.setDouble(6, latitude);
            pstmt.setDouble(7, radiusMeters / 1000.0); // 미터를 km로 변환
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PharmacyListItemDTO pharmacy = new PharmacyListItemDTO(
                        rs.getString("pharmacy_id"),
                        rs.getString("pharmacy_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getDouble("distance"),
                        rs.getString("operating_hours"),
                        rs.getBoolean("is_open"),
                        rs.getDouble("rating"),
                        rs.getString("status")
                    );
                    pharmacies.add(pharmacy);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 임시 더미 데이터 (DB 연결 전까지)
        if (pharmacies.isEmpty()) {
            pharmacies.add(new PharmacyListItemDTO(
                "pharmacy_001", "슬닥 약국", "서울 강남구 도산대로 174", 
                "02-1234-5678", 37.5665, 126.9780, 0.5, 
                "월-금 08:30-21:00", true, 4.5, "OPEN"
            ));
            pharmacies.add(new PharmacyListItemDTO(
                "pharmacy_002", "건강 약국", "서울 강남구 테헤란로 123", 
                "02-2345-6789", 37.5675, 126.9790, 1.2, 
                "월-금 09:00-20:00", true, 4.2, "OPEN"
            ));
            pharmacies.add(new PharmacyListItemDTO(
                "pharmacy_003", "행복 약국", "서울 강남구 논현로 456", 
                "02-3456-7890", 37.5655, 126.9770, 2.1, 
                "월-금 08:00-22:00", false, 4.8, "CLOSED"
            ));
        }

        return pharmacies;
    }

    /**
     * 약국 상세 정보 조회
     */
    public PharmacyListItemDTO getPharmacyById(String pharmacyId) {
        String sql = """
            SELECT 
                pharmacy_id,
                pharmacy_name,
                address,
                phone_number,
                latitude,
                longitude,
                0 AS distance,
                operating_hours,
                is_open,
                rating,
                status
            FROM pharmacies 
            WHERE pharmacy_id = ?
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pharmacyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PharmacyListItemDTO(
                        rs.getString("pharmacy_id"),
                        rs.getString("pharmacy_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getDouble("distance"),
                        rs.getString("operating_hours"),
                        rs.getBoolean("is_open"),
                        rs.getDouble("rating"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 임시 더미 데이터 (DB 연결 전까지)
        return new PharmacyListItemDTO(
            pharmacyId, "슬닥 약국", "서울 강남구 도산대로 174", 
            "02-1234-5678", 37.5665, 126.9780, 0.5, 
            "월-금 08:30-21:00", true, 4.5, "OPEN"
        );
    }
}
