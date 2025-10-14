package service;

import dao.PharmacyDAO;
import dto.PharmacyListItemDTO;

import java.util.List;
import java.util.UUID;

/**
 * 약국 서비스
 */
public class PharmacyService {

    private final PharmacyDAO pharmacyDAO = new PharmacyDAO();

    /**
     * 주변 약국 검색
     */
    public List<PharmacyListItemDTO> searchNearbyPharmacies(double latitude, double longitude, int radiusMeters) {
        // return pharmacyDAO.searchNearbyPharmacies(latitude, longitude, radiusMeters);
        // 테스트용 가라데이터 반환
        return getTestPharmacyData();
    }
    
    /**
     * 테스트용 약국 데이터
     */
    private List<PharmacyListItemDTO> getTestPharmacyData() {
        java.util.List<PharmacyListItemDTO> pharmacies = new java.util.ArrayList<>();
        
        // 약국 1 - 추천약국
        PharmacyListItemDTO pharmacy1 = new PharmacyListItemDTO();
        pharmacy1.setPharmacyId("pharmacy_001");
        pharmacy1.setPharmacyName("건강약국");
        pharmacy1.setAddress("서울시 강남구 도산대로 123");
        pharmacy1.setDistance(0.15); // 150m
        pharmacy1.setOpen(true);
        pharmacy1.setPhoneNumber("02-1234-5678");
        pharmacy1.setOperatingHours("09:00 - 22:00");
        pharmacy1.setRating(4.8);
        pharmacy1.setStatus("OPEN");
        pharmacies.add(pharmacy1);
        
        // 약국 2 - 일반약국
        PharmacyListItemDTO pharmacy2 = new PharmacyListItemDTO();
        pharmacy2.setPharmacyId("pharmacy_002");
        pharmacy2.setPharmacyName("메디컬약국");
        pharmacy2.setAddress("서울시 강남구 테헤란로 456");
        pharmacy2.setDistance(0.28); // 280m
        pharmacy2.setOpen(true);
        pharmacy2.setPhoneNumber("02-2345-6789");
        pharmacy2.setOperatingHours("08:00 - 23:00");
        pharmacy2.setRating(4.5);
        pharmacy2.setStatus("OPEN");
        pharmacies.add(pharmacy2);
        
        // 약국 3 - 영업종료
        PharmacyListItemDTO pharmacy3 = new PharmacyListItemDTO();
        pharmacy3.setPharmacyId("pharmacy_003");
        pharmacy3.setPharmacyName("24시간약국");
        pharmacy3.setAddress("서울시 강남구 압구정로 789");
        pharmacy3.setDistance(0.42); // 420m
        pharmacy3.setOpen(false);
        pharmacy3.setPhoneNumber("02-3456-7890");
        pharmacy3.setOperatingHours("24시간 운영");
        pharmacy3.setRating(4.2);
        pharmacy3.setStatus("CLOSED");
        pharmacies.add(pharmacy3);
        
        return pharmacies;
    }

    /**
     * 약국 상세 정보 조회
     */
    public PharmacyListItemDTO getPharmacyDetail(String pharmacyId) {
        return pharmacyDAO.getPharmacyById(pharmacyId);
    }

    /**
     * 처방전을 약국으로 전송
     */
    public String sendPrescriptionToPharmacy(String pharmacyId, String[] prescriptionIds) {
        // 실제 구현에서는 처방전을 약국 시스템으로 전송하는 로직이 들어감
        // 현재는 조제 ID만 생성하여 반환
        
        String dispensingId = UUID.randomUUID().toString();
        
        // TODO: 실제 처방전 전송 로직 구현
        // 1. 처방전 데이터를 약국 시스템으로 전송
        // 2. QR 코드 생성
        // 3. 조제 상태 초기화
        
        return dispensingId;
    }
}
