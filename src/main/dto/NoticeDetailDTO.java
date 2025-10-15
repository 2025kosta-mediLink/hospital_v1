package dto;

import java.sql.Timestamp;

public class NoticeDetailDTO {
    private Long id;
    private String content;
    private Timestamp startsAt;
    private Timestamp endsAt;

    public NoticeDetailDTO(Long id, String content, Timestamp startsAt, Timestamp endsAt) {
        this.id = id;
        this.content = content;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getStartsAt() { return startsAt; }
    public void setStartsAt(Timestamp startsAt) { this.startsAt = startsAt; }
    public Timestamp getEndsAt() { return endsAt; }
    public void setEndsAt(Timestamp endsAt) { this.endsAt = endsAt; }
}