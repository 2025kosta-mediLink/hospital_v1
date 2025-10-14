package dao;

import dto.DoctorSelectDTO;
import common.util.DBConnectionUtil;
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
}