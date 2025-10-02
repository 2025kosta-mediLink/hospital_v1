package service;

import dao.SymptomDAO;
import dto.SymptomDTO;

import java.util.List;

public class SymptomService {
  private final SymptomDAO dao = new SymptomDAO();

  public List<SymptomDTO> getSymptoms() {
    return dao.findAll();
  }
}
