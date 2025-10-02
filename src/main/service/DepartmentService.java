package service;

import dao.DepartmentDAO;
import dto.DepartmentDTO;

import java.util.List;

public class DepartmentService {

  private final DepartmentDAO dao = new DepartmentDAO();

  public List<DepartmentDTO> getDepartments() {
    return dao.findAll();
  }

}
