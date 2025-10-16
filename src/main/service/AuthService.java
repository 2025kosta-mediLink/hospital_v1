// src/main/java/service/AuthService.java
package service;

import dao.MemberDAO;
import domain.Member;
import dto.MemberSessionDTO;

public class AuthService {
    private final MemberDAO memberDAO = new MemberDAO();

    public boolean isLoginIdAvailable(String loginId) {
        return !memberDAO.existsByLoginId(loginId);
    }

    public Long signUp(String loginId, String rawPassword, String name,
                       String phone, String gender, String address, String rrn) {

        Member m = Member.register(loginId, rawPassword, name, phone, gender, address, rrn);
        return memberDAO.insert(m);
    }

    public MemberSessionDTO login(String loginId, String rawPassword) {
        Member m = memberDAO.findByLoginId(loginId);
        if (m == null) return null;
        if (!m.matchesPassword(rawPassword)) return null;

        return new MemberSessionDTO(
                m.getMemberId(),
                m.getUuid(),
                m.getName(),
                m.getPhone()
        );
    }
}
