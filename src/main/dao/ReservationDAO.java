package dao;

import common.util.DBConnectionUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReservationDAO {

    public boolean exists(long doctorId, String appointmentAt) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE doctor_id=? AND appointment_at=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setString(2, appointmentAt);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void insert(long memberId, long doctorId, String appointmentAt, String no) {
        String sql = "INSERT INTO reservation(member_id, doctor_id, reservation_no, appointment_at, status, created_at) " +
                "VALUES(?,?,?,?, 'RESERVED', NOW())";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            ps.setLong(2, doctorId);
            ps.setString(3, no);
            ps.setString(4, appointmentAt);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // "HH:MM" 집합
    public Set<String> findTimesForDoctorOnDate(long doctorId, LocalDate date) {
        String sql = "SELECT DATE_FORMAT(appointment_at, '%H:%i') AS hhmm " +
                "FROM reservation WHERE doctor_id=? AND DATE(appointment_at)=?";
        Set<String> set = new HashSet<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) set.add(rs.getString("hhmm"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return set;
    }
}
