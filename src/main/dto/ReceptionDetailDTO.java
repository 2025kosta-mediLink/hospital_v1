package dto;

public class ReceptionDetailDTO {
    private Long receptionId;
    private String receptionNo;
    private String status;
    private String doctorName;
    private String departmentName;

    public Long getReceptionId() { return receptionId; }
    public void setReceptionId(Long receptionId) { this.receptionId = receptionId; }

    public String getReceptionNo() { return receptionNo; }
    public void setReceptionNo(String receptionNo) { this.receptionNo = receptionNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
