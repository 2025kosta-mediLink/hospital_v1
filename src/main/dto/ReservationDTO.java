package dto;

public class ReservationDTO {

    private Long reservationId;
    private Long doctorId;
    private Long memberId;
    private String appointmentAt;
    private String status;
    private String reservationNo;  // 예약 번호 추가

    // Getters and Setters
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getAppointmentAt() {
        return appointmentAt;
    }

    public void setAppointmentAt(String appointmentAt) {
        this.appointmentAt = appointmentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReservationNo() {
        return reservationNo;  // 예약 번호 getter 추가
    }

    public void setReservationNo(String reservationNo) {
        this.reservationNo = reservationNo;  // 예약 번호 setter 추가
    }
}
