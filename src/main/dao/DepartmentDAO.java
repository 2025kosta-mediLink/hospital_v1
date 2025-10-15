package dao;

import dto.DepartmentSelectDTO;
import dto.DepartmentDetailDTO;
import common.util.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;

public class DepartmentDAO {
  public DepartmentSelectDTO findAllDepartments() {
    String sql = "SELECT department_id, name FROM department";
    DepartmentSelectDTO dto = new DepartmentSelectDTO();
    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        dto.getDepartments().add(new DepartmentDetailDTO(rs.getLong("department_id"), rs.getString("name")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return dto;
  }

  public DepartmentSelectDTO searchDepartments(String searchTerm) {
    String sql = "SELECT department_id, name FROM department WHERE name LIKE ?";
    DepartmentSelectDTO dto = new DepartmentSelectDTO();
    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, "%" + searchTerm + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          dto.getDepartments().add(new DepartmentDetailDTO(rs.getLong("department_id"), rs.getString("name")));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return dto;
  }
}