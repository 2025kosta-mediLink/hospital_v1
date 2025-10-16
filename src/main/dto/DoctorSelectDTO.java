package dto;

import java.util.ArrayList;
import java.util.List;

public class DoctorSelectDTO {
    private Long id;
    private String name;
    private String profileImageUrl;
    private List<NoticeDetailDTO> notices = new ArrayList<>();
    private List<ScheduleDetailDTO> schedule = new ArrayList<>();

    // Constructors, getters, setters
    public DoctorSelectDTO() {}

    public DoctorSelectDTO(Long id, String name, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public List<NoticeDetailDTO> getNotices() { return notices; }
    public void setNotices(List<NoticeDetailDTO> notices) { this.notices = notices; }
    public List<ScheduleDetailDTO> getSchedule() { return schedule; }
    public void setSchedule(List<ScheduleDetailDTO> schedule) { this.schedule = schedule; }
}