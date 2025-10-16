package controller;

import common.util.AuthSessionUtil;
import common.util.JsonUtil;
import dto.DoctorDetailDTO;
import dto.ReservationDTO;
import service.DoctorService;
import service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/v1/reservation/date-selection",
        "/v1/reservation/slots",
        "/v1/reservation/create",
        "/v1/reservation/complete"   // ← 추가
})
public class ReservationController extends HttpServlet {

    private ReservationService reservationService;
    private DoctorService doctorService;

    @Override
    public void init() {
        reservationService = new ReservationService();
        doctorService = new DoctorService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // 1) 날짜/시간 선택 페이지
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

        // 3) 예약 완료 페이지
        if ("/v1/reservation/complete".equals(path)) {
            String uuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
            if (uuid == null) return;
            String idStr = req.getParameter("reservationId");
            if (idStr == null) {
                resp.sendError(400, "예약 ID가 필요합니다.");
                return;
            }

            Long reservationId = Long.parseLong(idStr);

            if (!reservationService.isOwnedByUuid(reservationId, uuid)) {
                resp.sendError(403, "예약한 사용자와 현재 세션의 사용자가 일치하지 않습니다.");
                return;
            }

            // DTO 조회
            ReservationDTO reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                resp.sendError(404, "예약을 찾을 수 없습니다.");
                return;
            }

            DoctorDetailDTO doctor = doctorService.getDoctorById(reservation.getDoctorId());

            req.setAttribute("reservation", reservation);
            req.setAttribute("doctor", doctor);
            req.getRequestDispatcher("/WEB-INF/views/reservation/reservationComplete.jsp").forward(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String path = req.getServletPath();
        if (!"/v1/reservation/create".equals(path)) {
            resp.sendError(404);
            return;
        }

        String memberUuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
        if (memberUuid == null) return;

        long doctorId = Long.parseLong(req.getParameter("doctorId"));
        String appointmentAt = req.getParameter("appointmentAt"); // "YYYY-MM-DD HH:mm:ss"

        try {
            // create가 예약 PK를 리턴해야 합니다.
            Long reservationId = reservationService.createReservationByUuid(memberUuid, doctorId, appointmentAt);

            String ctx = req.getContextPath();
            resp.sendRedirect(ctx + "/v1/reservation/complete?reservationId=" + reservationId); // ← id 붙여서 이동
        } catch (IllegalStateException e) {
            req.setAttribute("error", "이미 해당 시간은 예약이 찼습니다. 다른 시간을 선택해주세요.");
            req.getRequestDispatcher("/WEB-INF/views/reservation/dateSelection.jsp").forward(req, resp);
        }
    }
}