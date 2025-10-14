package controller;

import dto.PharmacyListItemDTO;
import service.PharmacyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * 약국 검색 및 처방전 전송 컨트롤러
 */
@WebServlet("/v1/pharmacy/*")
public class PharmacyController extends HttpServlet {

    private final PharmacyService pharmacyService = new PharmacyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/search".equals(pathInfo)) {
            showPharmacySearch(req, resp);
        } else if ("/detail".equals(pathInfo)) {
            showPharmacyDetail(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/send".equals(pathInfo)) {
            sendPrescriptionToPharmacy(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showPharmacySearch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String[] selectedPrescriptions = (String[]) session.getAttribute("selectedPrescriptions");
        
        if (selectedPrescriptions == null || selectedPrescriptions.length == 0) {
            resp.sendRedirect(req.getContextPath() + "/v1/prescription");
            return;
        }

        try {
            // 현재 위치 기반 약국 검색 (임시로 더미 데이터 사용)
            double latitude = 37.5665; // 서울시청 위도 (임시)
            double longitude = 126.9780; // 서울시청 경도 (임시)
            
            List<PharmacyListItemDTO> pharmacies = pharmacyService.searchNearbyPharmacies(latitude, longitude, 3000); // 3km
            req.setAttribute("pharmacies", pharmacies);
            req.setAttribute("selectedPrescriptions", selectedPrescriptions);
            
            req.getRequestDispatcher("/WEB-INF/views/prescription/pharmacySearch.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "약국 검색 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/views/prescription/pharmacySearch.jsp").forward(req, resp);
        }
    }

    private void showPharmacyDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pharmacyId = req.getParameter("pharmacyId");
        if (pharmacyId == null || pharmacyId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/v1/pharmacy/search");
            return;
        }

        try {
            PharmacyListItemDTO pharmacy = pharmacyService.getPharmacyDetail(pharmacyId);
            if (pharmacy == null) {
                resp.sendRedirect(req.getContextPath() + "/v1/pharmacy/search");
                return;
            }
            
            req.setAttribute("pharmacy", pharmacy);
            req.getRequestDispatcher("/WEB-INF/views/prescription/pharmacyDetail.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "약국 정보를 불러오는 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/views/prescription/pharmacyDetail.jsp").forward(req, resp);
        }
    }

    private void sendPrescriptionToPharmacy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pharmacyId = req.getParameter("pharmacyId");
        HttpSession session = req.getSession();
        String[] selectedPrescriptions = (String[]) session.getAttribute("selectedPrescriptions");
        
        if (pharmacyId == null || selectedPrescriptions == null || selectedPrescriptions.length == 0) {
            resp.sendRedirect(req.getContextPath() + "/v1/prescription");
            return;
        }

        try {
            // 처방전을 약국으로 전송
            String dispensingId = pharmacyService.sendPrescriptionToPharmacy(pharmacyId, selectedPrescriptions);
            
            // 조제 현황 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/v1/dispensing/status?dispensingId=" + dispensingId);
        } catch (Exception e) {
            req.setAttribute("error", "처방전 전송 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/views/prescription/pharmacySearch.jsp").forward(req, resp);
        }
    }
}
