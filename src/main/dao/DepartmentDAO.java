// DepartmentDAO.java
package dao;

import common.DBConnectionUtil;
import dto.DepartmentListItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

  public List<DepartmentListItemDTO> findAll() {
    List<DepartmentListItemDTO> list = new ArrayList<>();
    String sql = "SELECT department_id, name FROM department ORDER BY name ASC";

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        DepartmentListItemDTO d = new DepartmentListItemDTO();
        d.setDepartmentId(rs.getLong("department_id"));
        d.setName(rs.getString("name"));
        list.add(d);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  // ✅ 추가: ID로 단일 조회
  public DepartmentListItemDTO findById(Long departmentId) {
    String sql = "SELECT department_id, name FROM department WHERE department_id = ?";
    DepartmentListItemDTO dto = null;

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, departmentId);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          dto = new DepartmentListItemDTO();
          dto.setDepartmentId(rs.getLong("department_id"));
          dto.setName(rs.getString("name"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dto;
  }
}
