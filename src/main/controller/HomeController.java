package controller;

import common.util.AuthSessionUtil;
import dto.HomeTodaySummaryDTO;
import service.HomeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * 홈 컨트롤러: 로그인 여부와 무관하게 접근 허용.
 * - 로그인 시: uuid 기반 개인화 요약
 * - 비로그인 시: 게스트용 요약
 */
@WebServlet({"/", "/v1/home"})
public class HomeController extends HttpServlet {

  private final HomeService homeService = new HomeService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uuid = AuthSessionUtil.getUuidOrNull(req);
    boolean isLoggedIn = (uuid != null);

    HomeTodaySummaryDTO summary = isLoggedIn
        ? homeService.getHomeByUuid(uuid)   // 개인화
        : homeService.getHomeForGuest();    // 게스트용

    req.setAttribute("summary", summary);
    req.setAttribute("isLoggedIn", isLoggedIn);
    req.setAttribute("loginUrl", req.getContextPath() + AuthSessionUtil.LOGIN_PAGE_PATH);

    req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
  }
}
