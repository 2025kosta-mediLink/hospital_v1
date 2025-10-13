package dao;

import common.DBConnectionUtil;
import dto.HomeTodaySummaryDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * 홈 상단 요약(오늘 예약 1건 + 내 대기 순번 1건) DAO.
 */
public class HomeDAO {

    // 오늘 첫 예약 1건만 (표시용 라벨 포함)
    private static final String SQL_TODAY_FIRST_RESERVATION = """
        SELECT r.appointment_at,
               dpt.name AS department_name,
               doc.name AS doctor_name
        FROM reservation r
        JOIN doctor doc ON r.doctor_id = doc.doctor_id
        JOIN department dpt ON doc.department_id = dpt.department_id
        WHERE r.member_id = ?
          AND DATE(r.appointment_at) = CURDATE()
          AND r.status IN ('RESERVED','DONE')
        ORDER BY r.appointment_at ASC
        LIMIT 1
        """;

    // 내가 가진 가장 최근 대기 티켓의 번호 (대기/호출/진료중 상태만)
    private static final String SQL_MY_LATEST_WAITING = """
        SELECT wt.queue_no
        FROM waiting_ticket wt
        JOIN reception rc ON wt.reception_id = rc.reception_id
        WHERE rc.member_id = ?
          AND wt.status IN ('WAITING','CALLED','IN_SERVICE')
        ORDER BY wt.created_at DESC
        LIMIT 1
        """;

    public HomeTodaySummaryDTO getHomeSummary(long memberId) {
        HomeTodaySummaryDTO dto = new HomeTodaySummaryDTO();

        try (Connection con = DBConnectionUtil.getConnection()) {

            // (1) 오늘 첫 예약 한 건
            try (PreparedStatement ps = con.prepareStatement(SQL_TODAY_FIRST_RESERVATION)) {
                ps.setLong(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp apAt = rs.getTimestamp("appointment_at");
                        dto.setAppointmentAt(apAt);
                        dto.setDepartmentName(rs.getString("department_name"));
                        dto.setDoctorName(rs.getString("doctor_name"));
                    }
                }
            }

            // (2) 나의 최근 대기 순번
            try (PreparedStatement ps = con.prepareStatement(SQL_MY_LATEST_WAITING)) {
                ps.setLong(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dto.setMyQueueNo(rs.getInt("queue_no"));
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("홈 요약 조회 실패", e);
        }

        return dto;
    }
}
