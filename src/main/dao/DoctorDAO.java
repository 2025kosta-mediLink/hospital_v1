package dao;

import common.DBConnectionUtil;
import dto.DoctorDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
  public List<DoctorDTO> findByDepartment(Long departmentId) {
    List<DoctorDTO> list = new ArrayList<>();
    String sql = "SELECT d.doctor_id,\n" +
        "       d.name,\n" +
        "       d.department_id,\n" +
        "       dep.name AS department_name\n" +
        "FROM doctor d\n" +
        "JOIN department dep ON d.department_id = dep.department_id\n" +
        "WHERE d.department_id = ?;";

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, departmentId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          DoctorDTO dto = new DoctorDTO();
          dto.setDoctorId(rs.getLong("doctor_id"));
          dto.setName(rs.getString("name"));
          dto.setDepartmentId(rs.getLong("department_id"));
          dto.setDepartmentName(rs.getString("department_name"));
          list.add(dto);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }
}
