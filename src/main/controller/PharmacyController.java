package controller;

import dto.PharmacyListItemDTO;
import service.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 약국 검색 및 처방전 전송 컨트롤러
 */
@WebServlet("/v1/pharmacy/*")
public class PharmacyController extends HttpServlet {

    private final PharmacyService pharmacyService = new PharmacyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/search".equals(pathInfo)) {
            showPharmacySearch(req, resp);
        } else if ("/detail".equals(pathInfo)) {
            showPharmacyDetail(req, resp);
        } else if ("/api/list".equals(pathInfo)) {
            getPharmacyListApi(req, resp);
        } else if ("/api/detail".equals(pathInfo)) {
            getPharmacyDetailApi(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/send".equals(pathInfo)) {
            sendPrescriptionToPharmacy(req, resp);
        } else if ("/api/search".equals(pathInfo)) {
            searchPharmaciesApi(req, resp);
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

    // API: 약국 목록 조회 (공공데이터 연동)
    private void getPharmacyListApi(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        try {
            int pageNo = Integer.parseInt(req.getParameter("pageNo") != null ? req.getParameter("pageNo") : "1");
            int numOfRows = Integer.parseInt(req.getParameter("numOfRows") != null ? req.getParameter("numOfRows") : "20");
            
            List<PharmacyListItemDTO> pharmacies = pharmacyService.getPharmacyListFromPublicData(pageNo, numOfRows);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", pharmacies,
                "pageNo", pageNo,
                "numOfRows", numOfRows
            );
            
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "약국 목록을 불러오는 중 오류가 발생했습니다."
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // API: 약국 상세 정보 조회
    private void getPharmacyDetailApi(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        String pharmacyId = req.getParameter("pharmacyId");
        if (pharmacyId == null || pharmacyId.trim().isEmpty()) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "약국 ID가 필요합니다."
            );
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        try {
            PharmacyListItemDTO pharmacy = pharmacyService.getPharmacyDetailFromPublicData(pharmacyId);
            
            if (pharmacy == null) {
                Map<String, Object> errorResponse = Map.of(
                    "success", false,
                    "message", "약국 정보를 찾을 수 없습니다."
                );
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            Map<String, Object> response = Map.of(
                "success", true,
                "data", pharmacy
            );
            
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "약국 정보를 불러오는 중 오류가 발생했습니다."
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // API: 위치 기반 약국 검색
    private void searchPharmaciesApi(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        try {
            // JSON 요청 본문 읽기
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                jsonBuilder.append(line);
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> requestData = objectMapper.readValue(jsonBuilder.toString(), Map.class);
            
            double latitude = Double.parseDouble(requestData.get("latitude").toString());
            double longitude = Double.parseDouble(requestData.get("longitude").toString());
            int radius = Integer.parseInt(requestData.getOrDefault("radius", "2000").toString());
            
            List<PharmacyListItemDTO> pharmacies = pharmacyService.searchNearbyPharmaciesFromPublicData(latitude, longitude, radius);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", pharmacies,
                "center", Map.of("latitude", latitude, "longitude", longitude),
                "radius", radius
            );
            
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "약국 검색 중 오류가 발생했습니다."
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
