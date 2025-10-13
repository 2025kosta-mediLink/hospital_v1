package dto;

public class DoctorListItemDTO {
  private Long doctorId;        // 의사 ID
  private String name;          // 의사 이름
  private Long departmentId;    // 소속 진료과 ID
  private String departmentName; // 소속 진료과 이름

  public Long getDoctorId() { return doctorId; }
  public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Long getDepartmentId() { return departmentId; }
  public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

  public String getDepartmentName() { return departmentName; }
  public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
