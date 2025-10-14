package service;

import dao.DepartmentDAO;
import dto.DepartmentSelectDTO;

public class DepartmentService {
  private DepartmentDAO departmentDAO;

  public DepartmentService() {
    departmentDAO = new DepartmentDAO();
  }

  public DepartmentSelectDTO getAllDepartments() {
    return departmentDAO.findAllDepartments();
  }

  public DepartmentSelectDTO searchDepartments(String searchTerm) {
    return departmentDAO.searchDepartments(searchTerm);
  }
}