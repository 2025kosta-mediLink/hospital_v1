package domain;

import java.util.Date;

public class DoctorNotice {
    private Long noticeId;
    private Long doctorId;
    private String content;
    private Date startsAt;
    private Date endsAt;
    private Integer priority;
    private Date createdAt;
    private Date updatedAt;

    public DoctorNotice() {}

    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getStartsAt() { return startsAt; }
    public void setStartsAt(Date startsAt) { this.startsAt = startsAt; }
    public Date getEndsAt() { return endsAt; }
    public void setEndsAt(Date endsAt) { this.endsAt = endsAt; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}