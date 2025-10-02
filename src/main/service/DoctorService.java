package service;

import dao.DoctorDAO;
import dto.DoctorDTO;

import java.util.List;

public class DoctorService {
  private final DoctorDAO dao = new DoctorDAO();

  public List<DoctorDTO> getDoctorsByDepartment(Long departmentId) {
    return dao.findByDepartment(departmentId);
  }
}
