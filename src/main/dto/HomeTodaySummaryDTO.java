package dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 홈 화면 상단 요약 패널 DTO.
 * - 오늘 "첫" 예약 일정(있다면)
 * - 나의 최근 대기 순번(있다면)
 */
public class HomeTodaySummaryDTO implements Serializable {

  // 오늘 첫 예약 시간 (없으면 null)
  private Date appointmentAt;

  // 예약이 있을 때 표시할 진료과/의료진
  private String departmentName;
  private String doctorName;

  // 내가 보유한 가장 최근 대기 티켓의 번호 (없으면 null)
  private Integer myQueueNo;

  // ===== 기존 getters/setters (그대로 유지) =====
  public Date getAppointmentAt() { return appointmentAt; }
  public void setAppointmentAt(Date appointmentAt) { this.appointmentAt = appointmentAt; }

  public String getDepartmentName() { return departmentName; }
  public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

  public String getDoctorName() { return doctorName; }
  public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

  public Integer getMyQueueNo() { return myQueueNo; }
  public void setMyQueueNo(Integer myQueueNo) { this.myQueueNo = myQueueNo; }

  // ===== 편의 메서드 (JSP에서 쓰기 좋게) =====

  /** 예약 정보가 있는지 */
  public boolean hasAppointment() {
    return appointmentAt != null;
  }

  /** 대기 번호가 있는지 */
  public boolean hasQueue() {
    return myQueueNo != null;
  }

  /** 예약 시간 텍스트(HH:mm), 없으면 빈 문자열 */
  public String getAppointmentTimeText() {
    if (appointmentAt == null) return "";
    return new SimpleDateFormat("HH:mm").format(appointmentAt);
  }

  /** 의사/진료과 표시 라벨 ("피부과 · 김의사"), 없으면 빈 문자열 */
  public String getDoctorLabel() {
    if (departmentName == null && doctorName == null) return "";
    if (departmentName == null) return doctorName;
    if (doctorName == null) return departmentName;
    return departmentName + " · " + doctorName;
  }

  /** 모든 정보가 비어있는지(게스트 기본 상태 등) */
  public boolean isEmpty() {
    return appointmentAt == null && myQueueNo == null
        && (departmentName == null || departmentName.isBlank())
        && (doctorName == null || doctorName.isBlank());
  }

  /** 게스트/빈 DTO 팩토리 */
  public static HomeTodaySummaryDTO empty() {
    return new HomeTodaySummaryDTO();
  }

  @Override
  public String toString() {
    return "HomeTodaySummaryDTO{" +
        "appointmentAt=" + appointmentAt +
        ", departmentName='" + departmentName + '\'' +
        ", doctorName='" + doctorName + '\'' +
        ", myQueueNo=" + myQueueNo +
        '}';
  }
}
