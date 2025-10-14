package dao;

import dto.NoticeDetailDTO;
import common.util.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorNoticeDAO {
    public List<NoticeDetailDTO> findNoticesByDoctorId(Long doctorId) {
        String sql = "SELECT notice_id, content, starts_at, ends_at FROM doctor_notice WHERE doctor_id = ?";
        List<NoticeDetailDTO> notices = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notices.add(new NoticeDetailDTO(rs.getLong("notice_id"), rs.getString("content"), rs.getTimestamp("starts_at"), rs.getTimestamp("ends_at")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }
}