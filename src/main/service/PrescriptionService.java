package service;

import dao.PrescriptionDAO;
import dto.PrescriptionListItemDTO;

import java.util.List;

/**
 * 처방전 서비스
 */
public class PrescriptionService {

    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    /**
     * 회원의 처방전 목록 조회
     */
    public List<PrescriptionListItemDTO> getPrescriptionList(long memberId) {
        return prescriptionDAO.getPrescriptionListByMemberId(memberId);
    }

    /**
     * 접수 데이터를 기반으로 처방전 생성
     */
    public Long createPrescriptionFromReception(Long receptionId, String content) {
        return prescriptionDAO.createPrescriptionFromReception(receptionId, content);
    }

    /**
     * 처방전을 약국으로 전송
     */
    public Long sendPrescriptionToPharmacy(Long prescriptionId, String pharmacyName, String pharmacyAddress) {
        return prescriptionDAO.sendPrescriptionToPharmacy(prescriptionId, pharmacyName, pharmacyAddress);
    }

    /**
     * 조제 상태 업데이트
     */
    public boolean updateDispensingStatus(Long pharmacyPrescriptionId, String status) {
        return prescriptionDAO.updateDispensingStatus(pharmacyPrescriptionId, status);
    }

    /**
     * 수령 완료 처리
     */
    public boolean completePickup(Long pharmacyPrescriptionId, String verifiedBy) {
        return prescriptionDAO.completePickup(pharmacyPrescriptionId, verifiedBy);
    }
}
