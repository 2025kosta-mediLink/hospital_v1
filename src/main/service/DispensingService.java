package service;

import dao.DispensingDAO;
import dao.PrescriptionDAO;
import dto.DispensingStatusDTO;

import java.time.LocalDateTime;

/**
 * 조제 서비스
 */
public class DispensingService {

    private final DispensingDAO dispensingDAO = new DispensingDAO();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    /**
     * 조제 현황 조회
     */
    public DispensingStatusDTO getDispensingStatus(String dispensingId) {
        // return dispensingDAO.getDispensingStatus(dispensingId);
        // 테스트용 가라데이터 반환
        return getTestDispensingStatus(dispensingId);
    }
    
    /**
     * 테스트용 조제 상태 데이터
     */
    private DispensingStatusDTO getTestDispensingStatus(String dispensingId) {
        DispensingStatusDTO status = new DispensingStatusDTO();
        status.setDispensingId(dispensingId);
        status.setPharmacyName("건강약국");
        status.setPharmacyAddress("서울시 강남구 도산대로 123");
        status.setPharmacyPhone("02-1234-5678");
        status.setPharmacyLatitude(37.5665);
        status.setPharmacyLongitude(126.9780);
        
        // 테스트용 상태 설정 - 조제 완료 상태로 고정
        status.setStatus("COMPLETED");
        status.setDispenserName("김약사");
        status.setReceivedAt("14:15");
        status.setCompletedAt("14:30");
        status.setEstimatedCompletionTime("14:30");
        
        return status;
    }

    /**
     * 조제 상태 업데이트
     */
    public boolean updateDispensingStatus(String dispensingId, String status, String dispenserName, 
                                        LocalDateTime estimatedCompletionTime, LocalDateTime completedAt) {
        return dispensingDAO.updateDispensingStatus(dispensingId, status, dispenserName, estimatedCompletionTime, completedAt);
    }

    /**
     * 수령 완료 처리
     */
    public boolean completeReceipt(String dispensingId) {
        // 수령 완료 처리
        boolean success = dispensingDAO.completeReceipt(dispensingId);
        
        if (success) {
            // 모든 처방전을 완료 상태로 업데이트 (테스트용)
            prescriptionDAO.updateAllPrescriptionsCompleted(
                "서대문권약국", 
                java.time.LocalDate.now().toString()
            );
        }
        
        return success;
    }
}
