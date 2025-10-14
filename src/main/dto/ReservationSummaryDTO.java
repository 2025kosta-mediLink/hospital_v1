// src/main/java/dto/ReservationSummaryDTO.java
package dto;

/** 오늘 예약 리스트용 요약 DTO */
public class ReservationSummaryDTO {
  private long reservationId;
  private String reservationNo;   // reservation.reservation_no
  private String timeText;        // "HH:mm" (appointment_at 포맷)
  private String departmentName;
  private String doctorName;

  public long getReservationId() { return reservationId; }
  public void setReservationId(long reservationId) { this.reservationId = reservationId; }
  public String getReservationNo() { return reservationNo; }
  public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
  public String getTimeText() { return timeText; }
  public void setTimeText(String timeText) { this.timeText = timeText; }
  public String getDepartmentName() { return departmentName; }
  public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
  public String getDoctorName() { return doctorName; }
  public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
}
