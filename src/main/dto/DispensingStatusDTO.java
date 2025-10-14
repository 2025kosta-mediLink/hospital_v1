package dto;

import java.time.LocalDateTime;

/**
 * 조제 현황 DTO
 */
public class DispensingStatusDTO {
    private String dispensingId;
    private String pharmacyName;
    private String pharmacyAddress;
    private String pharmacyPhone;
    private double pharmacyLatitude;
    private double pharmacyLongitude;
    private String status; // RECEIVED, DISPENSING, COMPLETED, RECEIVED_BY_USER
    private String receivedAt;
    private String dispenserName;
    private String estimatedCompletionTime;
    private String completedAt;
    private String prescriptionDetails;
    private String qrCode;

    public DispensingStatusDTO() {}

    public DispensingStatusDTO(String dispensingId, String pharmacyName, String pharmacyAddress, 
                             String pharmacyPhone, double pharmacyLatitude, double pharmacyLongitude,
                             String status, String receivedAt, String dispenserName, 
                             String estimatedCompletionTime, String completedAt,
                             String prescriptionDetails, String qrCode) {
        this.dispensingId = dispensingId;
        this.pharmacyName = pharmacyName;
        this.pharmacyAddress = pharmacyAddress;
        this.pharmacyPhone = pharmacyPhone;
        this.pharmacyLatitude = pharmacyLatitude;
        this.pharmacyLongitude = pharmacyLongitude;
        this.status = status;
        this.receivedAt = receivedAt;
        this.dispenserName = dispenserName;
        this.estimatedCompletionTime = estimatedCompletionTime;
        this.completedAt = completedAt;
        this.prescriptionDetails = prescriptionDetails;
        this.qrCode = qrCode;
    }

    // Getters and Setters
    public String getDispensingId() {
        return dispensingId;
    }

    public void setDispensingId(String dispensingId) {
        this.dispensingId = dispensingId;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacyAddress() {
        return pharmacyAddress;
    }

    public void setPharmacyAddress(String pharmacyAddress) {
        this.pharmacyAddress = pharmacyAddress;
    }

    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    public void setPharmacyPhone(String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }

    public double getPharmacyLatitude() {
        return pharmacyLatitude;
    }

    public void setPharmacyLatitude(double pharmacyLatitude) {
        this.pharmacyLatitude = pharmacyLatitude;
    }

    public double getPharmacyLongitude() {
        return pharmacyLongitude;
    }

    public void setPharmacyLongitude(double pharmacyLongitude) {
        this.pharmacyLongitude = pharmacyLongitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getDispenserName() {
        return dispenserName;
    }

    public void setDispenserName(String dispenserName) {
        this.dispenserName = dispenserName;
    }

    public String getEstimatedCompletionTime() {
        return estimatedCompletionTime;
    }

    public void setEstimatedCompletionTime(String estimatedCompletionTime) {
        this.estimatedCompletionTime = estimatedCompletionTime;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getPrescriptionDetails() {
        return prescriptionDetails;
    }

    public void setPrescriptionDetails(String prescriptionDetails) {
        this.prescriptionDetails = prescriptionDetails;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
