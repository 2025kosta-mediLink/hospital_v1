package dao;

import dto.DoctorSelectDTO;
import common.util.DBConnectionUtil;
import dto.DoctorDetailDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
  public List<DoctorSelectDTO> findDoctorsByDepartment(Long departmentId) {
    String sql = "SELECT doctor_id, name, profile_image_url FROM doctor WHERE" +
            " department_id = ?";
    List<DoctorSelectDTO> doctors = new ArrayList<>();
    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, departmentId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          DoctorSelectDTO dto = new DoctorSelectDTO(rs.getLong("doctor_id"),
                  rs.getString("name"),
                  rs.getString("profile_image_url"));
          doctors.add(dto);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return doctors;
  }
    public DoctorDetailDTO findDoctorById(long doctorId) {
        String sql =
                "SELECT d.doctor_id, d.name, d.profile_image_url, dep.name AS department_name " +
                        "FROM doctor d JOIN department dep ON dep.department_id = d.department_id " +
                        "WHERE d.doctor_id = ?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DoctorDetailDTO(
                            rs.getLong("doctor_id"),
                            rs.getString("name"),
                            rs.getString("department_name"),
                            rs.getString("profile_image_url")
                    );
                }
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    // 의사 ID로 부서 ID 조회
    public Long findDepartmentIdByDoctorId(long doctorId) {
        String sql = "SELECT department_id FROM doctor WHERE doctor_id = ?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long depId = rs.getLong("department_id");
                    return rs.wasNull() ? null : depId;
                }
                return null; // 의사 없음
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}