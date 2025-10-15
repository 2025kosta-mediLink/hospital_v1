package dao;

import common.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DoctorExceptionDayDAO {
    public String findType(long doctorId, LocalDate date) {
        String sql = "SELECT type FROM doctor_exception_day WHERE doctor_id=? AND exception_date=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("type") : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
