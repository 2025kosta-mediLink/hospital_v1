package dto;

import java.time.LocalDateTime;

/**
 * 처방전 목록 아이템 DTO
 */
public class PrescriptionListItemDTO {
    private Long prescriptionId;
    private String departmentName;
    private String doctorName;
    private String treatmentDate;
    private String status; // PENDING, DISPENSING, COMPLETED, RECEIVED
    private String pharmacyName;
    private LocalDateTime completedDate;
    private boolean canSelect;
    private boolean completed;

    public PrescriptionListItemDTO() {}

    public PrescriptionListItemDTO(Long prescriptionId, String departmentName, String doctorName, 
                                 String treatmentDate, String status, String pharmacyName, 
                                 LocalDateTime completedDate, boolean canSelect) {
        this.prescriptionId = prescriptionId;
        this.departmentName = departmentName;
        this.doctorName = doctorName;
        this.treatmentDate = treatmentDate;
        this.status = status;
        this.pharmacyName = pharmacyName;
        this.completedDate = completedDate;
        this.canSelect = canSelect;
        this.completed = "RECEIVED".equals(status) || "COMPLETED".equals(status);
    }

    // Getters and Setters
    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(String treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public boolean isCanSelect() {
        return canSelect;
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
