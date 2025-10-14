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
        // 테스트용 가라데이터 반환
        return getTestPrescriptionData();
    }
    
    /**
     * 테스트용 처방전 데이터
     */
    private List<PrescriptionListItemDTO> getTestPrescriptionData() {
        java.util.List<PrescriptionListItemDTO> prescriptions = new java.util.ArrayList<>();
        
        // 처방전 1 - 선택 가능 (내과)
        PrescriptionListItemDTO prescription1 = new PrescriptionListItemDTO();
        prescription1.setPrescriptionId(1L);
        prescription1.setTreatmentDate("2025-01-15");
        prescription1.setDepartmentName("내과");
        prescription1.setDoctorName("김의사");
        prescription1.setCanSelect(true);
        prescription1.setStatus("처방완료");
        prescriptions.add(prescription1);
        
        // 처방전 2 - 선택 가능 (정형외과)
        PrescriptionListItemDTO prescription2 = new PrescriptionListItemDTO();
        prescription2.setPrescriptionId(2L);
        prescription2.setTreatmentDate("2025-01-14");
        prescription2.setDepartmentName("정형외과");
        prescription2.setDoctorName("박의사");
        prescription2.setCanSelect(true);
        prescription2.setStatus("처방완료");
        prescriptions.add(prescription2);
        
        return prescriptions;
    }

    /**
     * 처방전 상태 업데이트
     */
    public boolean updatePrescriptionStatus(long prescriptionId, String status, String pharmacyName, java.time.LocalDateTime completedDate) {
        return prescriptionDAO.updatePrescriptionStatus(prescriptionId, status, pharmacyName, completedDate);
    }
}
