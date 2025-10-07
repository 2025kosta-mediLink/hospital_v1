package dto;

public class DepartmentDTO {
  private Long departmentId; // 진료과 ID
  private String name;       // 진료과 이름 (예: 내과, 외과)

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
