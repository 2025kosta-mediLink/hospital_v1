package controller;

import common.util.AuthSessionUtil;
import common.util.JsonUtil;
import dto.ReservationListDTO;
import service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebServlet(urlPatterns = {
        "/v1/reservation/list",
        "/v1/reservation/cancel"
})
public class ReservationListController extends HttpServlet {

    private ReservationService reservationService;
    private String kakaoJsKey;

    @Override
    public void init() throws ServletException {
        reservationService = new ReservationService();
        try {
            // 프로퍼티 파일 로딩
            Properties props = new Properties();
            InputStream input = getServletContext().getResourceAsStream("/WEB-INF/classes/application.properties");
            if (input == null) {
                throw new ServletException("Unable to find application.properties");
            }
            props.load(input);
            kakaoJsKey = props.getProperty("kakao.javascript.key");
        } catch (IOException e) {
            throw new ServletException("Error loading properties file", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
        if (uuid == null) return;

        String month  = req.getParameter("month");   // "YYYY-MM" or null
        String status = req.getParameter("status");  // "ALL|RESERVED|DONE|CANCELLED" or null

        ReservationListDTO result =
                reservationService.getReservationList(uuid, month, status);

        req.setAttribute("grouped", result.getGroupedByMonth());
        req.setAttribute("monthOptions", result.getMonthOptions());
        req.setAttribute("selectedMonth", result.getSelectedMonth());
        req.setAttribute("selectedStatus", result.getSelectedStatus());
        req.setAttribute("kakaoJsKey", kakaoJsKey); // 카카오 키를 JSP로 전달
        req.getRequestDispatcher("/WEB-INF/views/reservation/reservationList.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getServletPath();
        if ("/v1/reservation/cancel".equals(path)) {
            // 로그인 필수 (401)
            String uuid = AuthSessionUtil.requireUuidOr401(req, resp);
            if (uuid == null) return;

            String idStr = req.getParameter("reservationId");
            if (idStr == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "reservationId is required");
                return;
            }
            long reservationId;
            try { reservationId = Long.parseLong(idStr); }
            catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid reservationId");
                return;
            }

            var result = reservationService.cancelReservation(uuid, reservationId);

            resp.setContentType("application/json; charset=UTF-8");
            var payload = new java.util.HashMap<String, Object>();
            payload.put("ok", result.isOk());
            payload.put("message", result.getMessage());
            payload.put("status", result.getStatus());
            resp.getWriter().write(JsonUtil.toJson(payload));
            return;
        }

        // 그 외 POST는 404
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
