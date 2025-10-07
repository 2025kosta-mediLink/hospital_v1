package service;

import dao.DoctorDAO;
import dto.DoctorDTO;
import java.util.List;

public class DoctorService {
  private final DoctorDAO dao = new DoctorDAO();

  // 진료과 ID로 의사 목록 조회
  public List<DoctorDTO> getDoctorsByDepartment(Long departmentId) {
    return dao.findByDepartment(departmentId);
  }

  // ✅ 추가: ID로 의사 이름 조회
  public String findNameById(Long doctorId) {
    DoctorDTO doctor = dao.findById(doctorId);
    return (doctor != null) ? doctor.getName() : null;
  }
}
