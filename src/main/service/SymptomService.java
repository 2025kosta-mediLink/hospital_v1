package service;

import dao.SymptomDAO;
import dto.SymptomListItemDTO;

import java.util.List;

public class SymptomService {
  private final SymptomDAO dao = new SymptomDAO();

  public List<SymptomListItemDTO> getSymptoms() {
    return dao.findAll();
  }
}
