package service;

import dao.HomeDAO;
import dto.HomeTodaySummaryDTO;

/**
 * 홈 요약 Service.
 */
public class HomeService {
  private final HomeDAO homeDAO = new HomeDAO();

  /** (기존) memberId 기반 조회 - 필요시 유지 */
//  public HomeTodaySummaryDTO getHome(long memberId){
//    return homeDAO.getHomeSummary(memberId);
//  }

  /** (신규) uuid 기반 개인화 조회 */
  public HomeTodaySummaryDTO getHomeByUuid(String uuid){
    if (uuid == null || uuid.isBlank()) {
      return getHomeForGuest();
    }
    return homeDAO.getHomeSummaryByUuid(uuid);
  }

  /** (신규) 비로그인 게스트용 요약 */
  public HomeTodaySummaryDTO getHomeForGuest(){
    // 게스트 기본값: 비어 있는 DTO 반환 (null/빈값)
    // 필요하면 병원 운영시간/공지 등 공개 정보 세팅 가능
    return new HomeTodaySummaryDTO();
  }
}
