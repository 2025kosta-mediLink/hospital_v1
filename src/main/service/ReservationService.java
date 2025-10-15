package service;

import common.type.ReservationStatus;
import dao.MemberDAO;
import dao.ReservationDAO;
import dao.DoctorWeeklyScheduleDAO;
import dao.DoctorExceptionDayDAO;
import dto.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    public Long createReservationByUuid(String memberUuid, long doctorId, String appointmentAt) {
        Long memberId = memberDAO.findIdByUuid(memberUuid);
        if (memberId == null) {
            throw new IllegalStateException("member-not-found");
        }

        if (reservationDAO.exists(doctorId, appointmentAt)) {
            throw new IllegalStateException("occupied");
        }
        String no = genNo();
        return reservationDAO.insert(memberId, doctorId, appointmentAt, no);  // reservationId 리턴
    }

    public ReservationDTO getReservationById(Long reservationId) {
        return reservationDAO.findById(reservationId);
    }

    public ReservationListDTO getReservationList(String memberUuid, String month, String statusUi) {
        Long memberId = memberDAO.findIdByUuid(memberUuid);
        if (memberId == null) throw new IllegalStateException("member-not-found");

        // UI → enum → DB 상태값
        ReservationStatus st = ReservationStatus.fromUiOrNull(statusUi);
        String dbStatus = (st == null) ? null : st.name();

        List<ReservationListItemDTO> rows =
                reservationDAO.findListByMember(memberId, month, dbStatus);

        // 날짜/시간 한글 표기 + 상태 라벨/배지 세팅
        for (ReservationListItemDTO it : rows) {
            fillKoreanDateTime(it);
            if (it.getStatus() != null) {
                try {
                    ReservationStatus s = ReservationStatus.valueOf(it.getStatus());
                    it.setStatusLabel(s.getLabelKo());
                    it.setStatusBadge(s.getBadgeClass());
                } catch (IllegalArgumentException ignore) { /* DB 값이 예외면 무시 */ }
            }
        }

        // 월 옵션
        List<String> ymList = rows.stream()
                .map(ReservationListItemDTO::getYearMonth)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        List<MonthOptionDTO> monthOptions = ymList.stream()
                .map(ym -> new MonthOptionDTO(ym, formatMonthKorean(ym)))
                .collect(Collectors.toList());

        // 월별 그룹핑 (내림차순 유지)
        Map<String, List<ReservationListItemDTO>> grouped =
                rows.stream().collect(Collectors.groupingBy(
                        ReservationListItemDTO::getYearMonth,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        ReservationListDTO result = new ReservationListDTO();
        result.setGroupedByMonth(grouped);
        result.setMonthOptions(monthOptions);
        result.setSelectedMonth(month);
        result.setSelectedStatus(
                (statusUi == null || statusUi.isBlank()) ? "ALL" : statusUi.toUpperCase(Locale.ROOT)
        );
        return result;
    }

    /** 예약 취소 */
    public ReservationCancelDTO cancelReservation(String memberUuid, long reservationId) {
        Long memberId = memberDAO.findIdByUuid(memberUuid);
        if (memberId == null) {
            return new ReservationCancelDTO(false, "회원 정보를 확인할 수 없습니다.", null);
        }

        ReservationOptionDTO option = reservationDAO.findOptionById(reservationId);
        if (option == null) {
            return new ReservationCancelDTO(false, "예약을 찾을 수 없습니다.", null);
        }
        if (!memberId.equals(option.getMemberId())) {
            return new ReservationCancelDTO(false, "본인 예약만 취소할 수 있습니다.", option.getStatus());
        }

        // 상태 검사
        if ("DONE".equalsIgnoreCase(option.getStatus())) {
            return new ReservationCancelDTO(false, "접수완료된 예약은 취소할 수 없습니다.", "DONE");
        }
        if ("CANCELLED".equalsIgnoreCase(option.getStatus())) {
            return new ReservationCancelDTO(true, "이미 취소된 예약입니다.", "CANCELLED"); // idempotent OK
        }

        int updated = reservationDAO.cancelIfOwnerAndReserved(reservationId, memberId);
        if (updated == 1) {
            return new ReservationCancelDTO(true, "예약이 취소되었습니다.", "CANCELLED");
        }
        // RESERVED가 아니어서 업데이트 못한 경우(경쟁 상태 등)
        return new ReservationCancelDTO(false, "취소할 수 없는 상태입니다.", option.getStatus());
    }

    private static String formatMonthKorean(String ym) {
        // "YYYY-MM" -> "2024년 1월"
        String[] p = ym.split("-");
        return Integer.parseInt(p[0]) + "년 " + Integer.parseInt(p[1]) + "월";
    }

    private static void fillKoreanDateTime(ReservationListItemDTO it) {
        // appointmentAt: "YYYY-MM-DD HH:mm"
        LocalDateTime dt = LocalDateTime.parse(it.getAppointmentAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String[] dows = {"월","화","수","목","금","토","일"};
        int dowIdx = dt.getDayOfWeek().getValue()-1; // 0=월
        String dateLabel = dt.getYear() + "년 " + dt.getMonthValue() + "월 " + dt.getDayOfMonth() + "일 (" + dows[dowIdx] + ")";
        String ampm = (dt.getHour() < 12) ? "오전" : "오후";
        int hour12 = dt.getHour() % 12; if (hour12 == 0) hour12 = 12;
        String timeLabel = ampm + " " + String.format("%d:%02d", hour12, dt.getMinute());

        it.setDateLabel(dateLabel);
        it.setTimeLabel(timeLabel);
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
