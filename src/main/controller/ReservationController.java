package controller;

import common.util.AuthSessionUtil;
import common.util.JsonUtil;
import service.ReservationService;
import service.DoctorService;
import dto.DoctorDetailDTO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/v1/reservation/date-selection",
        "/v1/reservation/slots",
        "/v1/reservation/create"
})
public class ReservationController extends HttpServlet {
    private ReservationService reservationService;
    private DoctorService doctorService;

    @Override public void init() {
        reservationService = new ReservationService();
        doctorService = new DoctorService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // 1) 날짜/시간 선택 페이지 (로그인 필수)
        if ("/v1/reservation/date-selection".equals(path)) {
            String uuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
            if (uuid == null) return; // 로그인 페이지로 리다이렉트됨

            long doctorId = Long.parseLong(req.getParameter("doctorId"));
            DoctorDetailDTO doctor = doctorService.getDoctorById(doctorId);
            req.setAttribute("doctor", doctor);
            req.getRequestDispatcher("/WEB-INF/views/reservation/dateSelection.jsp").forward(req, resp);
            return;
        }

        // 2) 타임슬롯 API (비로그인도 허용하려면 아래 줄을 주석 처리)
        if ("/v1/reservation/slots".equals(path)) {
            // 요구사항상 로그인 필수면 401, 아니면 다음 줄을 지워도 됨
            if (AuthSessionUtil.requireUuidOr401(req, resp) == null) return;

            long doctorId = Long.parseLong(req.getParameter("doctorId"));
            String date = req.getParameter("date"); // YYYY-MM-DD

            Map<String, Object> slots = reservationService.getAvailableSlots(doctorId, date);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(JsonUtil.toJson(slots));
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String path = req.getServletPath();
        if (!"/v1/reservation/create".equals(path)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String memberUuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
        if (memberUuid == null) return;

        long doctorId = Long.parseLong(req.getParameter("doctorId"));
        String appointmentAt = req.getParameter("appointmentAt"); // "YYYY-MM-DD HH:mm:ss"

        try {
            reservationService.createReservationByUuid(memberUuid, doctorId, appointmentAt);
            resp.sendRedirect(req.getContextPath() + "/v1/reservation/complete");
        } catch (IllegalStateException e) {
            req.setAttribute("error", "이미 해당 시간은 예약이 찼습니다. 다른 시간을 선택해주세요.");
            req.getRequestDispatcher("/WEB-INF/views/reservation/dateSelection.jsp").forward(req, resp);
        }
    }
}
