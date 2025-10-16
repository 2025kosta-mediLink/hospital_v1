// src/main/java/domain/Member.java
package domain;

import java.time.LocalDateTime;
import java.util.UUID;
import common.util.PasswordUtil;

public class Member {
    private Long memberId;
    private String uuid;
    private String loginId;
    private String passwordHash;
    private String name;
    private String phone;
    private String gender;   // "M"/"F"
    private String address;
    private String rrn;      // 민감정보: 외부 노출 금지
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deleteAt;

    public Member() {}

    // 회원가입용 팩토리
    public static Member register(String loginId, String rawPassword, String name,
                                  String phone, String gender, String address, String rrn) {
        Member m = new Member();
        m.uuid = UUID.randomUUID().toString();
        m.loginId = loginId;
        m.passwordHash = PasswordUtil.sha256(rawPassword);
        m.name = name;
        m.phone = phone;
        m.gender = gender;
        m.address = address;
        m.rrn = rrn;
        return m;
    }

    // 인증/행위
    public boolean matchesPassword(String rawPassword){
        return PasswordUtil.sha256(rawPassword).equals(this.passwordHash);
    }
    public void changePassword(String rawPassword){
        this.passwordHash = PasswordUtil.sha256(rawPassword);
    }
    public void softDelete(LocalDateTime now){ this.deleteAt = now; }

    // Getter/Setter (필요 범위)
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getUuid() { return uuid; }
    public String getLoginId() { return loginId; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getRrn() { return rrn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeleteAt() { return deleteAt; }

    public void setUuid(String uuid) { this.uuid = uuid; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setRrn(String rrn) { this.rrn = rrn; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setDeleteAt(LocalDateTime deleteAt) { this.deleteAt = deleteAt; }
}
