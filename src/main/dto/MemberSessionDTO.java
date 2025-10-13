package dto;

public class MemberSessionDTO {
    private final Long memberId;
    private final String uuid;
    private final String name;
    private final String phone;

    public MemberSessionDTO(Long memberId, String uuid, String name, String phone) {
        this.memberId = memberId;
        this.uuid = uuid;
        this.name = name;
        this.phone = phone;
    }
    public Long getMemberId() { return memberId; }
    public String getUuid() { return uuid; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}
