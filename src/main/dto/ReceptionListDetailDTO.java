package dto;

/** 접수 상세 조회 DTO (camelCase 필드) */
public class ReceptionListDetailDTO {
    private Long receptionId;       // 접수 ID
    private String receptionNo;    // 접수 번호
    private String type;            // 'RESERVATION' | 'DIRECT'
    private String status;          // 'WAITING' | 'IN_SERVICE' | 'DONE' | 'CANCELLED'
    private Boolean consentNotice;  // 개인정보 안내 동의 여부
    private String consentAt;       // 동의 시각 (yyyy-MM-dd HH:mm)
    private String noteToDoctor;    // 전달사항
    private String createdAt;       // 생성 시각
    private String updatedAt;       // 수정 시각

    private String doctorName;      // 의사명
    private String departmentName;  // 진료과명
    private String symptomNames;    // 증상명 목록 (콤마 구분)

    public Long getReceptionId() { return receptionId; }
    public void setReceptionId(Long receptionId) { this.receptionId = receptionId; }

    public String getReceptionNo() { return receptionNo; }
    public void setReceptionNo(String receptionNo) { this.receptionNo = receptionNo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getConsentNotice() { return consentNotice; }
    public void setConsentNotice(Boolean consentNotice) { this.consentNotice = consentNotice; }

    public String getConsentAt() { return consentAt; }
    public void setConsentAt(String consentAt) { this.consentAt = consentAt; }

    public String getNoteToDoctor() { return noteToDoctor; }
    public void setNoteToDoctor(String noteToDoctor) { this.noteToDoctor = noteToDoctor; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getSymptomNames() { return symptomNames; }
    public void setSymptomNames(String symptomNames) { this.symptomNames = symptomNames; }
}
