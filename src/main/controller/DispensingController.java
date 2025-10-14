package controller;

import dto.DispensingStatusDTO;
import service.DispensingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * 조제 현황 컨트롤러: 조제 상태 조회 및 수령 완료 처리
 */
@WebServlet("/v1/dispensing/*")
public class DispensingController extends HttpServlet {

    private final DispensingService dispensingService = new DispensingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/status".equals(pathInfo)) {
            showDispensingStatus(req, resp);
        } else if ("/complete".equals(pathInfo)) {
            completeReceipt(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if ("/complete".equals(pathInfo)) {
            completeReceipt(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showDispensingStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String dispensingId = req.getParameter("dispensingId");
        if (dispensingId == null || dispensingId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/v1/prescription");
            return;
        }

        try {
            DispensingStatusDTO status = dispensingService.getDispensingStatus(dispensingId);
            if (status == null) {
                resp.sendRedirect(req.getContextPath() + "/v1/prescription");
                return;
            }
            
            req.setAttribute("status", status);
            req.getRequestDispatcher("/WEB-INF/views/prescription/dispensingStatus.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "조제 현황을 불러오는 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/views/prescription/dispensingStatus.jsp").forward(req, resp);
        }
    }

    private void completeReceipt(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String dispensingId = req.getParameter("dispensingId");
        if (dispensingId == null || dispensingId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/v1/prescription");
            return;
        }

        try {
            // 수령 완료 처리
            boolean success = dispensingService.completeReceipt(dispensingId);
            
            if (success) {
                // 처방전 목록으로 리다이렉트 (완료 상태 표시)
                resp.sendRedirect(req.getContextPath() + "/v1/prescription?completed=true");
            } else {
                req.setAttribute("error", "수령 완료 처리 중 오류가 발생했습니다.");
                req.getRequestDispatcher("/WEB-INF/views/prescription/dispensingStatus.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", "수령 완료 처리 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/views/prescription/dispensingStatus.jsp").forward(req, resp);
        }
    }
}
