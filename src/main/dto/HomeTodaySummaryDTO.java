package dto;

import java.util.Date;

/**
 * 홈 화면 상단 요약 패널에 들어갈 최소 정보 DTO.
 * - 오늘 "첫" 예약 일정(있다면)
 * - 나의 최근 대기 순번(있다면)
 */
public class HomeTodaySummaryDTO {

    // 오늘 첫 예약 시간 (없으면 null)
    private Date appointmentAt;

    // 예약이 있을 때 표시할 진료과/의료진
    private String departmentName;
    private String doctorName;

    // 내가 보유한 가장 최근 대기 티켓의 번호 (없으면 null)
    private Integer myQueueNo;

    // --- getters/setters ---
    public Date getAppointmentAt() { return appointmentAt; }
    public void setAppointmentAt(Date appointmentAt) { this.appointmentAt = appointmentAt; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public Integer getMyQueueNo() { return myQueueNo; }
    public void setMyQueueNo(Integer myQueueNo) { this.myQueueNo = myQueueNo; }
}
