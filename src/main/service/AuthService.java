package service;

import dao.MemberDAO;
import dto.MemberDTO;
import dto.MemberSessionDTO;
import common.util.PasswordUtil;

public class AuthService {
    private final MemberDAO memberDAO = new MemberDAO();

    public boolean isLoginIdAvailable(String loginId) {
        return !memberDAO.existsByLoginId(loginId);
    }

    public Long signUp(String loginId, String rawPassword, String name,
                       String phone, String gender, String address, String rrn) {

        MemberDTO m = MemberDTO.register(loginId, rawPassword, name, phone, gender, address, rrn);
        return memberDAO.insert(m);
    }

    public MemberSessionDTO login(String loginId, String rawPassword) {
        MemberDTO m = memberDAO.findByLoginId(loginId);
        if (m == null) return null;
        if (!matchesPassword(rawPassword, m.getPasswordHash())) return null;

        return new MemberSessionDTO(
                m.getMemberId(),
                m.getUuid(),
                m.getName(),
                m.getPhone()
        );
    }

    public boolean matchesPassword(String rawPassword, String passwordHash) {
        return PasswordUtil.sha256(rawPassword).equals(passwordHash);
    }
}
