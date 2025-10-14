package dto;

/**
 * 약국 목록 아이템 DTO
 */
public class PharmacyListItemDTO {
    private String pharmacyId;
    private String pharmacyName;
    private String address;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private double distance; // km
    private String operatingHours;
    private boolean isOpen;
    private double rating;
    private String status; // OPEN, CLOSED, BREAK

    public PharmacyListItemDTO() {}

    public PharmacyListItemDTO(String pharmacyId, String pharmacyName, String address, 
                             String phoneNumber, double latitude, double longitude, 
                             double distance, String operatingHours, boolean isOpen, 
                             double rating, String status) {
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.operatingHours = operatingHours;
        this.isOpen = isOpen;
        this.rating = rating;
        this.status = status;
    }

    // Getters and Setters
    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
