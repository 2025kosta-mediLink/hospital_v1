package dao;

import common.DBConnectionUtil;
import dto.DoctorListItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

  // 진료과별 의사 목록 조회
  public List<DoctorListItemDTO> findByDepartment(Long departmentId) {
    List<DoctorListItemDTO> list = new ArrayList<>();

    // department_id로 doctor 조회 + department 테이블 조인
    String sql = "SELECT d.doctor_id, " +
            "d.name, " +
            "d.department_id, " +
            "dep.name AS department_name " +
            "FROM doctor d " +
            "JOIN department dep ON d.department_id = dep.department_id " +
            "WHERE d.department_id = ?;";

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      // ? 바인딩
      ps.setLong(1, departmentId);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          DoctorListItemDTO dto = new DoctorListItemDTO();
          dto.setDoctorId(rs.getLong("doctor_id"));
          dto.setName(rs.getString("name"));
          dto.setDepartmentId(rs.getLong("department_id"));
          dto.setDepartmentName(rs.getString("department_name"));
          list.add(dto);
        }
      }
    } catch (Exception e) {
      e.printStackTrace(); // 👉 실서비스라면 로깅으로 교체 필요
    }
    return list;
  }

  // ✅ 추가: ID로 단일 조회
  public DoctorListItemDTO findById(Long doctorId) {
    String sql = "SELECT d.doctor_id, d.name, d.department_id, dep.name AS department_name " +
            "FROM doctor d " +
            "JOIN department dep ON d.department_id = dep.department_id " +
            "WHERE d.doctor_id = ?";

    DoctorListItemDTO dto = null;

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          dto = new DoctorListItemDTO();
          dto.setDoctorId(rs.getLong("doctor_id"));
          dto.setName(rs.getString("name"));
          dto.setDepartmentId(rs.getLong("department_id"));
          dto.setDepartmentName(rs.getString("department_name"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dto;
  }
}
