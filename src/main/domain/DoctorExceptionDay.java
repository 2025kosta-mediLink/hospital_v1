package domain;

import java.util.Date;

public class DoctorExceptionDay {
    private Long exceptionId;
    private Long doctorId;
    private Date exceptionDate;
    private String type;
    private Date createdAt;
    private Date updatedAt;

    public DoctorExceptionDay() {}

    public Long getExceptionId() { return exceptionId; }
    public void setExceptionId(Long exceptionId) { this.exceptionId = exceptionId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Date getExceptionDate() { return exceptionDate; }
    public void setExceptionDate(Date exceptionDate) { this.exceptionDate = exceptionDate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}