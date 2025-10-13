package dao;

import common.util.DBConnectionUtil;
import dto.SymptomListItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SymptomDAO {
  public List<SymptomListItemDTO> findAll() {
    List<SymptomListItemDTO> list = new ArrayList<>();
    String sql = "SELECT symptom_id, name FROM symptom ORDER BY symptom_id";

    try (Connection conn = DBConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        SymptomListItemDTO dto = new SymptomListItemDTO();
        dto.setSymptomId(rs.getLong("symptom_id"));
        dto.setName(rs.getString("name"));
        list.add(dto);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }
}
