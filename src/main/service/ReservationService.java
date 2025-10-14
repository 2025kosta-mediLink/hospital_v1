package service;

import dao.MemberDAO;
import dao.ReservationDAO;
import dao.DoctorWeeklyScheduleDAO;
import dao.DoctorExceptionDayDAO;

import java.time.*;
import java.util.*;

public class ReservationService {
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DoctorWeeklyScheduleDAO weeklyDAO = new DoctorWeeklyScheduleDAO();
    private final DoctorExceptionDayDAO exceptionDAO = new DoctorExceptionDayDAO();
    private final MemberDAO memberDAO = new MemberDAO();

    // { "am":[...], "pm":[...] }
    public Map<String, Object> getAvailableSlots(long doctorId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        int dow = date.getDayOfWeek().getValue(); // 1=월..7=일

        var sched = weeklyDAO.findOneByDoctorIdAndDayOfWeek(doctorId, dow);

        List<String> am = new ArrayList<>();
        List<String> pm = new ArrayList<>();
        if (sched != null && sched.isAmFlag()) am.addAll(range("09:00","12:00",30));
        if (sched != null && sched.isPmFlag()) pm.addAll(range("13:00","17:00",30));

        String exType = exceptionDAO.findType(doctorId, date); // OFF, AM_OFF, PM_OFF, null
        if ("OFF".equals(exType)) { am.clear(); pm.clear(); }
        else if ("AM_OFF".equals(exType)) { am.clear(); }
        else if ("PM_OFF".equals(exType)) { pm.clear(); }

        Set<String> occupied = reservationDAO.findTimesForDoctorOnDate(doctorId, date);
        am.removeIf(occupied::contains);
        pm.removeIf(occupied::contains);

        Map<String, Object> res = new HashMap<>();
        res.put("am", am);
        res.put("pm", pm);
        return res;
    }

    public void createReservationByUuid(String memberUuid, long doctorId, String appointmentAt) {
        Long memberId = memberDAO.findIdByUuid(memberUuid);
        if (memberId == null) throw new IllegalStateException("member-not-found");

        if (reservationDAO.exists(doctorId, appointmentAt)) {
            throw new IllegalStateException("occupied");
        }
        String no = genNo();
        reservationDAO.insert(memberId, doctorId, appointmentAt, no);
    }

    private List<String> range(String start, String end, int minutes) {
        List<String> r = new ArrayList<>();
        LocalTime s = LocalTime.parse(start);
        LocalTime e = LocalTime.parse(end);
        while (!s.isAfter(e.minusMinutes(minutes))) {
            r.add(s.toString()); // "HH:MM"
            s = s.plusMinutes(minutes);
        }
        return r;
    }

    private String genNo() {
        return "R-" + LocalDate.now().toString().replace("-", "")
                + "-" + String.format("%04d", new Random().nextInt(10000));
    }
}
