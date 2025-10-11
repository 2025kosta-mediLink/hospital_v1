package service;

import dao.DepartmentDAO;
import dto.DepartmentListItemDTO;
import java.util.List;

public class DepartmentService {

  // DAO 객체 (DB 쿼리 수행 담당)
  private final DepartmentDAO dao = new DepartmentDAO();

  // 진료과 목록 조회 메서드
  public List<DepartmentListItemDTO> getDepartments() {
    // 단순히 DAO 호출 (추가 로직이 필요할 경우 여기서 처리 가능)
    return dao.findAll();
  }

  // ✅ 추가: ID로 진료과 이름 조회
  public String findNameById(Long departmentId) {
    DepartmentListItemDTO dept = dao.findById(departmentId);
    return (dept != null) ? dept.getName() : null;
  }
}
