package service;

import dto.DoctorSelectDTO;
import dao.DoctorDAO;
import dto.DoctorDetailDTO;

import java.util.List;

public class DoctorService {
  private DoctorDAO doctorDAO = new DoctorDAO();

  public List<DoctorSelectDTO> getDoctorsByDepartment(Long departmentId) {
    return doctorDAO.findDoctorsByDepartment(departmentId);
  }

  public DoctorDetailDTO getDoctorById(long doctorId) {
    return doctorDAO.findDoctorById(doctorId);
  }
}