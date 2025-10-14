// src/main/java/dao/MemberDAO.java
package dao;

import common.util.DBConnectionUtil;
import domain.Member;

import java.sql.*;

public class MemberDAO {

    public boolean existsByLoginId(String loginId) {
        String sql = "SELECT 1 FROM member WHERE login_id = ? AND delete_at IS NULL LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true; // 보수적 처리
        }
    }

    /** 회원가입: 도메인 엔티티 저장 */
    public Long insert(Member m) {
        String sql = "INSERT INTO member " +
                "(uuid, login_id, password, name, phone, gender, address, rrn, created_at, updated_at, delete_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NULL)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getUuid());
            ps.setString(2, m.getLoginId());
            ps.setString(3, m.getPasswordHash());
            ps.setString(4, m.getName());
            ps.setString(5, m.getPhone());
            ps.setString(6, m.getGender());
            ps.setString(7, m.getAddress());
            ps.setString(8, m.getRrn());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    m.setMemberId(id);
                    return id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 로그인/조회: 도메인으로 반환 */
    public Member findByLoginId(String loginId) {
        String sql = "SELECT member_id, uuid, login_id, password, name, phone, gender, address, rrn, " +
                "       created_at, updated_at, delete_at " +
                "FROM member WHERE login_id = ? AND delete_at IS NULL";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member();
                    m.setMemberId(rs.getLong("member_id"));
                    m.setUuid(rs.getString("uuid"));
                    m.setLoginId(rs.getString("login_id"));
                    m.setPasswordHash(rs.getString("password"));
                    m.setName(rs.getString("name"));
                    m.setPhone(rs.getString("phone"));
                    m.setGender(rs.getString("gender"));
                    m.setAddress(rs.getString("address"));
                    m.setRrn(rs.getString("rrn"));
                    Timestamp c = rs.getTimestamp("created_at");
                    Timestamp u = rs.getTimestamp("updated_at");
                    Timestamp d = rs.getTimestamp("delete_at");
                    if (c != null) m.setCreatedAt(c.toLocalDateTime());
                    if (u != null) m.setUpdatedAt(u.toLocalDateTime());
                    if (d != null) m.setDeleteAt(d.toLocalDateTime());
                    return m;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long findIdByUuid(String uuid) {
        String sql = "SELECT member_id FROM member WHERE uuid=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("member_id");
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
