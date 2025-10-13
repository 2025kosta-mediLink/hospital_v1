package controller;

import dto.HomeTodaySummaryDTO;
import service.HomeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * 홈 컨트롤러: 홈 상단 요약 패널 데이터 주입.
 */
@WebServlet("/v1/home")
public class HomeController extends HttpServlet {

    private final HomeService homeService = new HomeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long memberId = 1L; // 로그인 전 임시 고정
        HomeTodaySummaryDTO summary = homeService.getHome(memberId);
        req.setAttribute("summary", summary);

        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
