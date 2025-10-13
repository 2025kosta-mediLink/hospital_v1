package dto;

import java.time.LocalDateTime;
import java.util.Date;

public class ReceptionListItemDTO {

    private Long receptionId;
    private Integer receptionNo;
    private String type;          // DIRECT/RESERVATION
    private String status;        // WAITING/DONE/...
    private Long doctorId;
    private String doctorName;
    private String departmentName;
    private Date createdAt;

    public Long getReceptionId() { return receptionId; }
    public void setReceptionId(Long receptionId) { this.receptionId = receptionId; }

    public Integer getReceptionNo() { return receptionNo; }
    public void setReceptionNo(Integer receptionNo) { this.receptionNo = receptionNo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
