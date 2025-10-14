// src/main/java/dto/ReservationDetailDTO.java
package dto;

/** 접수 시작 전 상세 확인/프리필용 DTO */
public class ReservationDetailDTO {
  private long reservationId;
  private String reservationNo;
  private long departmentId;
  private long doctorId;
  private String departmentName;
  private String doctorName;
  private boolean alreadyReception;
  private Long receptionId;   // 이미 접수됐다면 값 세팅

  public long getReservationId() { return reservationId; }
  public void setReservationId(long reservationId) { this.reservationId = reservationId; }
  public String getReservationNo() { return reservationNo; }
  public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
  public long getDepartmentId() { return departmentId; }
  public void setDepartmentId(long departmentId) { this.departmentId = departmentId; }
  public long getDoctorId() { return doctorId; }
  public void setDoctorId(long doctorId) { this.doctorId = doctorId; }
  public String getDepartmentName() { return departmentName; }
  public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
  public String getDoctorName() { return doctorName; }
  public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
  public boolean isAlreadyReception() { return alreadyReception; }
  public void setAlreadyReception(boolean alreadyReception) { this.alreadyReception = alreadyReception; }
  public Long getReceptionId() { return receptionId; }
  public void setReceptionId(Long receptionId) { this.receptionId = receptionId; }
}
