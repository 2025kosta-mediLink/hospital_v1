package dao;

import common.util.DBConnectionUtil;
import dto.ReservationDTO;
import dto.ReservationOptionDTO;
import dto.ReservationListItemDTO;

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

    public Long insert(long memberId, long doctorId, String appointmentAt, String no) {
        String sql = "INSERT INTO reservation(member_id, doctor_id, reservation_no, appointment_at, status, created_at, updated_at) " +
                "VALUES(?,?,?,?, 'RESERVED', NOW(), NOW())";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, memberId);
            ps.setLong(2, doctorId);
            ps.setString(3, no);
            ps.setString(4, appointmentAt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);  // reservationId 리턴
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ReservationDTO findById(Long reservationId) {
        String sql = "SELECT reservation_id, doctor_id, member_id, reservation_no, appointment_at, status FROM reservation WHERE reservation_id = ?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReservationDTO reservation = new ReservationDTO();
                    reservation.setReservationId(rs.getLong("reservation_id"));
                    reservation.setDoctorId(rs.getLong("doctor_id"));
                    reservation.setMemberId(rs.getLong("member_id"));
                    reservation.setReservationNo(rs.getString("reservation_no"));
                    reservation.setAppointmentAt(rs.getString("appointment_at"));
                    reservation.setStatus(rs.getString("status"));
                    return reservation;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
    /** 회원 예약내역 조회 (월/상태 옵션 필터) */
    public List<ReservationListItemDTO> findListByMember(Long memberId, String month /*YYYY-MM*/, String status /*RESERVED/DONE/CANCELLED or null*/) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.reservation_id, r.reservation_no, r.status, " +
                        "       DATE_FORMAT(r.appointment_at,'%Y-%m-%d %H:%i') AS appointment_at, " +
                        "       DATE_FORMAT(r.appointment_at,'%Y-%m') AS ym, " +
                        "       d.name AS doctor_name, dp.name AS department_name " +
                        "FROM reservation r " +
                        "JOIN doctor d  ON r.doctor_id = d.doctor_id " +
                        "JOIN department dp ON d.department_id = dp.department_id " +
                        "WHERE r.member_id = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(memberId);

        if (month != null && !month.isBlank()) {
            sql.append(" AND DATE_FORMAT(r.appointment_at,'%Y-%m') = ? ");
            params.add(month);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND r.status = ? ");
            params.add(status);
        }

        sql.append(" ORDER BY r.appointment_at DESC ");

        List<ReservationListItemDTO> list = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservationListItemDTO dto = new ReservationListItemDTO();
                    dto.setReservationId(rs.getLong("reservation_id"));
                    dto.setReservationNo(rs.getString("reservation_no"));
                    dto.setStatus(rs.getString("status"));
                    dto.setAppointmentAt(rs.getString("appointment_at")); // "YYYY-MM-DD HH:mm"
                    dto.setYearMonth(rs.getString("ym"));
                    dto.setDoctorName(rs.getString("doctor_name"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 예약 기본 정보(소유자/상태) 조회 */
    public ReservationOptionDTO findOptionById(Long reservationId) {
        String sql = "SELECT reservation_id, member_id, status " +
                "FROM reservation WHERE reservation_id = ?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReservationOptionDTO dto = new ReservationOptionDTO();
                    dto.setReservationId(rs.getLong("reservation_id"));
                    dto.setMemberId(rs.getLong("member_id"));
                    dto.setStatus(rs.getString("status"));
                    return dto;
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    /**
     * RESERVED 상태일 때만 취소로 변경.
     * @return 변경된 행 수(성공 시 1)
     */
    public int cancelIfOwnerAndReserved(long reservationId, long memberId) {
        String sql = "UPDATE reservation " +
                "SET status='CANCELLED', updated_at=NOW() " +
                "WHERE reservation_id=? AND member_id=? AND status='RESERVED'";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            ps.setLong(2, memberId);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
