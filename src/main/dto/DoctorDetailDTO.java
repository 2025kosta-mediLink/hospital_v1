package dto;

public class DoctorDetailDTO {
    private long id;
    private String name;
    private String departmentName;
    private String profileImageUrl;

    public DoctorDetailDTO(long id, String name, String departmentName, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.departmentName = departmentName;
        this.profileImageUrl = profileImageUrl;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDepartmentName() { return departmentName; }
    public String getProfileImageUrl() { return profileImageUrl; }
}
