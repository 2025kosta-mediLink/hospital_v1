package dto;

public class ReservationListItemDTO {
    private Long reservationId;
    private String reservationNo;
    private String status;           // RESERVED / DONE / CANCELLED
    private String yearMonth;        // "YYYY-MM"
    private String appointmentAt;    // "YYYY-MM-DD HH:mm"
    private String dateLabel;        // "2024년 3월 20일 (수)"
    private String timeLabel;        // "오전 10:30"
    private String doctorName;
    private String departmentName;

    // 뷰 편의(선택): 상태 한글/배지
    private String statusLabel;      // "예약완료"
    private String statusBadge;      // "badge-green"

    // getters/setters
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
    public String getAppointmentAt() { return appointmentAt; }
    public void setAppointmentAt(String appointmentAt) { this.appointmentAt = appointmentAt; }
    public String getDateLabel() { return dateLabel; }
    public void setDateLabel(String dateLabel) { this.dateLabel = dateLabel; }
    public String getTimeLabel() { return timeLabel; }
    public void setTimeLabel(String timeLabel) { this.timeLabel = timeLabel; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }
    public String getStatusBadge() { return statusBadge; }
    public void setStatusBadge(String statusBadge) { this.statusBadge = statusBadge; }
}
