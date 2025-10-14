package dto;

import java.sql.Timestamp;

public class DoctorNoticeDTO {
    private Long noticeId;
    private String content;
    private Timestamp startsAt;
    private Timestamp endsAt;

    public DoctorNoticeDTO(Long noticeId, String content, Timestamp startsAt, Timestamp endsAt) {
        this.noticeId = noticeId;
        this.content = content;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Timestamp startsAt) {
        this.startsAt = startsAt;
    }

    public Timestamp getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Timestamp endsAt) {
        this.endsAt = endsAt;
    }
}
