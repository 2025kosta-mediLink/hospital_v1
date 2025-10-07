package service;

import dao.ReceptionDAO;
import dao.ReceptionListDAO;
import dto.ReceptionDetailDTO;
import dto.ReceptionListItemDTO;

import java.time.LocalDate;
import java.util.List;

public class ReceptionService {
  private final ReceptionDAO dao = new ReceptionDAO();
  private final ReceptionListDAO receptionListDAO = new ReceptionListDAO();

  /**
   * 접수 생성 (reception + reception_symptom)
   * - 하나의 트랜잭션으로 묶어서 저장
   */
  public Long createReception(Long memberId,
                              Long doctorId,
                              String[] symptomIds,
                              String noteToDoctor,
                              boolean consentNotice) {

    // 단일 DAO 메서드에서 트랜잭션 단위로 실행
    return dao.insertReceptionWithSymptoms(memberId, doctorId, symptomIds, noteToDoctor, consentNotice);
  }

  // ✅ 추가: 접수 상세 조회
  public ReceptionDetailDTO getReceptionDetail(Long receptionId) {
    return dao.findReceptionDetail(receptionId);
  }

  public List<ReceptionListItemDTO> getList(Long memberId,
                                            String status,
                                            LocalDate from,
                                            LocalDate to) {
    return receptionListDAO.findList(memberId, status, from, to);
  }
}
