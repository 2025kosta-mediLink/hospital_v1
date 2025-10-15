// src/main/java/controller/AuthController.java
package controller;

import dto.MemberSessionDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthService;

@WebServlet("/v1/auth/*")
public class AuthController extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || "/login".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }
        switch (path) {
            case "/consent" -> {
                req.getRequestDispatcher("/WEB-INF/views/auth/consent.jsp").forward(req, resp);
                return;
            }
            case "/sign-up" -> {
                req.getRequestDispatcher("/WEB-INF/views/auth/signUp.jsp").forward(req, resp);
                return;
            }
            case "/check-id" -> {
                String loginId = req.getParameter("loginId");
                if (loginId == null || loginId.isBlank()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "loginId is required");
                    return;
                }
                boolean available = authService.isLoginIdAvailable(loginId);
                resp.setContentType("text/plain; charset=UTF-8");
                resp.getWriter().write(available ? "available=true" : "available=false");
                return;
            }
            case "/sign-up-done" -> {
                req.getRequestDispatcher("/WEB-INF/views/auth/signUpDone.jsp").forward(req, resp);
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();

        if ("/sign-up".equals(path)) {
            String loginId = req.getParameter("loginId");
            String password = req.getParameter("password");
            String name = req.getParameter("name");
            String phone = req.getParameter("phone");
            String gender = req.getParameter("gender");
            String address = req.getParameter("address");
            String rrn = req.getParameter("rrn");

            if (loginId == null || password == null || name == null ||
                phone == null || gender == null || address == null || rrn == null ||
                loginId.isBlank() || password.isBlank() || name.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Required fields missing");
                return;
            }
            if (!authService.isLoginIdAvailable(loginId)) {
                req.setAttribute("error", "이미 사용 중인 아이디입니다.");
                req.getRequestDispatcher("/WEB-INF/views/auth/signUp.jsp").forward(req, resp);
                return;
            }

            Long memberId = authService.signUp(loginId, password, name, phone, gender, address,
                rrn);
            if (memberId == null) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sign-up failed");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/v1/auth/sign-up-done");
            return;
        }

        if ("/login".equals(path)) {
            String loginId = req.getParameter("loginId");
            String password = req.getParameter("password");

            if (loginId == null || password == null || loginId.isBlank() || password.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "loginId/password required");
                return;
            }

            MemberSessionDTO sessionUser = authService.login(loginId, password);
            if (sessionUser == null) {
                req.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("LOGIN_MEMBER", sessionUser);
            resp.sendRedirect(req.getContextPath() + "/v1/home");
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
