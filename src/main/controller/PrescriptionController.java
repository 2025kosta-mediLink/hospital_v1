package controller;

import dto.PrescriptionListItemDTO;
import service.PrescriptionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * 처방전 조회 컨트롤러: 처방전 목록 조회 및 선택 기능
 */
@WebServlet("/v1/prescription/*")
public class PrescriptionController extends HttpServlet {

    private final PrescriptionService prescriptionService = new PrescriptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || "/".equals(pathInfo)) {
            // 기본 처방전 목록 조회
            showPrescriptionList(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showPrescriptionList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long memberId = 1L; // 로그인 전 임시 고정
        
        try {
            List<PrescriptionListItemDTO> prescriptions = prescriptionService.getPrescriptionList(memberId);
            
            // 완료된 처방전 정보 처리
            String completed = req.getParameter("completed");
            String dispensingId = req.getParameter("dispensingId");
            
            if ("true".equals(completed) && dispensingId != null) {
                // 완료된 처방전 정보를 세션에 저장하여 JSP에서 사용할 수 있도록 함
                HttpSession session = req.getSession();
                session.setAttribute("completedDispensingId", dispensingId);
                session.setAttribute("completedDate", java.time.LocalDate.now().toString());
                session.setAttribute("completedPharmacyName", "건강약국"); // 실제로는 dispensingId로 약국명 조회
            }
            
            req.setAttribute("prescriptions", prescriptions);
            
            req.getRequestDispatcher("/WEB-INF/views/prescription/prescriptionList.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "처방전 목록을 불러오는 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/views/prescription/prescriptionList.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/select".equals(pathInfo)) {
            handlePrescriptionSelection(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void handlePrescriptionSelection(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String prescriptionIdsParam = req.getParameter("prescriptionIds");
        
        if (prescriptionIdsParam != null && !prescriptionIdsParam.trim().isEmpty()) {
            String[] selectedPrescriptionIds = prescriptionIdsParam.split(",");
            
            // 선택된 처방전들을 세션에 저장
            HttpSession session = req.getSession();
            session.setAttribute("selectedPrescriptions", selectedPrescriptionIds);
            
            // 약국 검색 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/v1/pharmacy/search");
            return;
        }
        
        // 오류 시 처방전 목록으로 돌아가기
        resp.sendRedirect(req.getContextPath() + "/v1/prescription");
    }
}
