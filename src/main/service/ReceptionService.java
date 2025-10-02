package service;

import dao.ReceptionDAO;

public class ReceptionService {
  private final ReceptionDAO dao = new ReceptionDAO();

  public Long createReception(Long memberId,
                              Long doctorId,
                              String[] symptomIds,
                              String noteToDoctor,
                              boolean consentNotice) {

    // 1. 접수 저장
    Long receptionId = dao.insertReception(memberId, doctorId, noteToDoctor, consentNotice);

    // 2. 접수-증상 매핑 저장
    dao.insertReceptionSymptom(receptionId, symptomIds);

    return receptionId;
  }
}
