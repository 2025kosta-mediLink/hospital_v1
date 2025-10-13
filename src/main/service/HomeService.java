package service;

import dao.HomeDAO;
import dto.HomeTodaySummaryDTO;

/**
 * 홈 요약 Service.
 */
public class HomeService {
    private final HomeDAO homeDAO = new HomeDAO();

    public HomeTodaySummaryDTO getHome(long memberId){
        return homeDAO.getHomeSummary(memberId);
    }
}
