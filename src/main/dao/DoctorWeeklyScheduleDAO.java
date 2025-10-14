package dao;

import domain.DoctorWeeklySchedule; // 수정된 도메인 클래스
import common.util.DBConnectionUtil;
import dto.ScheduleDetailDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorWeeklyScheduleDAO {

    public List<ScheduleDetailDTO> findScheduleByDoctorId(Long doctorId) {
        String sql = "SELECT day_of_week, am_flag, pm_flag " +
                "FROM doctor_weekly_schedule WHERE doctor_id = ?";
        List<ScheduleDetailDTO> scheduleList = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduleDetailDTO scheduleDetail = new ScheduleDetailDTO(
                            rs.getInt("day_of_week"),
                            rs.getBoolean("am_flag"),
                            rs.getBoolean("pm_flag")
                    );
                    scheduleList.add(scheduleDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scheduleList;
    }

    public ScheduleDetailDTO findOneByDoctorIdAndDayOfWeek(long doctorId, int dow) {
        String sql = "SELECT day_of_week, am_flag, pm_flag " +
                "FROM doctor_weekly_schedule WHERE doctor_id=? AND day_of_week=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setInt(2, dow);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ScheduleDetailDTO(
                            rs.getInt("day_of_week"),
                            rs.getBoolean("am_flag"),
                            rs.getBoolean("pm_flag")
                    );
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }
}