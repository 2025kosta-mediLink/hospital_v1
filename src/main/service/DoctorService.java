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

  // 의사 ID로 부서 ID 조회
  public Long getDepartmentIdByDoctorId(long doctorId) {
    return doctorDAO.findDepartmentIdByDoctorId(doctorId);
  }
}