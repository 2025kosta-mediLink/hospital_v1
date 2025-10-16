// src/main/java/controller/ReceptionGatewayController.java
package controller;

import common.util.AuthSessionUtil;
import dto.ReservationDetailDTO;
import dto.ReservationSummaryDTO;
import service.ReservationQueryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * 접수 진입 게이트웨이
 * - GET  /v1/reception/entry  : 홈→오늘 예약 여부 분기
 * - GET  /v1/reception/today  : 오늘 예약 목록 JSP 렌더
 * - POST /v1/reception/from-reservation : 예약에서 접수 시작
 */
@WebServlet(urlPatterns = {
    "/v1/reception/entry",
    "/v1/reception/today",
    "/v1/reception/from-reservation"
})
public class ReceptionGatewayController extends HttpServlet {

  // 인터페이스 없이 구현체 사용
  private final ReservationQueryService reservationService = new ReservationQueryService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    // 로그인 강제 (없으면 로그인 화면으로)
    String memberUuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
    if (memberUuid == null) return;

    String path = req.getServletPath();

    switch (path) {
      case "/v1/reception/entry": {
        boolean hasToday = reservationService.existsTodayByMemberUuid(memberUuid);
        if (hasToday) {
          resp.sendRedirect(req.getContextPath() + "/v1/reception/today");
        } else {
          resp.sendRedirect(req.getContextPath() + "/v1/reception/departments");
        }
        break;
      }
      case "/v1/reception/today": {
        List<ReservationSummaryDTO> list = reservationService.findTodayByMemberUuid(memberUuid);
        req.setAttribute("reservations", list);
        req.getRequestDispatcher("/WEB-INF/views/reception/receptionToday.jsp").forward(req, resp);
        break;
      }
      default:
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    String memberUuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
    if (memberUuid == null) return;

    String path = req.getServletPath();

    if ("/v1/reception/from-reservation".equals(path)) {
      long reservationId = Long.parseLong(req.getParameter("reservationId"));

      // 예약 상세 + 이미 접수했는지 여부
      ReservationDetailDTO detail = reservationService.getDetailForReception(memberUuid, reservationId);
      if (detail == null) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "예약 정보가 존재하지 않습니다.");
        return;
      }
      if (detail.isAlreadyReception()) {
        // 이미 접수된 경우: 상세로 이동
        resp.sendRedirect(req.getContextPath() + "/v1/reception/detail?receptionId=" + detail.getReceptionId());
        return;
      }

      // 접수 프리필(진료과/의사) 세션 저장 → 증상 입력 화면으로
      HttpSession session = req.getSession();
      session.setAttribute("prefillDepartmentId", detail.getDepartmentId());
      session.setAttribute("prefillDoctorId", detail.getDoctorId());

      resp.sendRedirect(req.getContextPath() + "/v1/reception/symptom");
      return;
    }

    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
  }
}
