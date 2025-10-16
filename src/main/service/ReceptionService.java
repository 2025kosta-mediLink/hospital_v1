package service;

import dao.ReceptionDAO;
import dao.ReceptionListDAO;
import dto.ReceptionDetailDTO;
import dto.ReceptionListDetailDTO;
import dto.ReceptionListItemDTO;

import java.time.LocalDate;
import java.util.List;

public class ReceptionService {
    private final ReceptionDAO dao = new ReceptionDAO();

    /**
     * 접수 생성 (reception + reception_symptom)
     * - 하나의 트랜잭션으로 묶어서 저장
     * - 로그인 사용자 uuid를 받아 내부에서 member_id로 해석
     */
    public Long createReception(String uuid,
                                Long doctorId,
                                String[] symptomIds,
                                String noteToDoctor,
                                boolean consentNotice) {

        // 단일 DAO 메서드에서 트랜잭션 단위로 실행
        return dao.insertReceptionWithSymptoms(uuid, doctorId, symptomIds, noteToDoctor, consentNotice);
    }

    // 접수 상세 조회
    public ReceptionDetailDTO getReceptionDetail(Long receptionId) {
        return dao.findReceptionDetail(receptionId);
    }

}
