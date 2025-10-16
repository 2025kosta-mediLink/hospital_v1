package common.util;

import dto.MemberSessionDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AuthSessionUtil
 * - 로그인 세션 확인/가져오기/리다이렉트 공통 유틸
 * - 세션 키: LOGIN_MEMBER_ATTR
 */
public final class AuthSessionUtil {

  public static final String LOGIN_MEMBER_ATTR = "LOGIN_MEMBER";
  public static final String LOGIN_PAGE_PATH = "/v1/auth/login";
  private static final Integer DEFAULT_SESSION_TIMEOUT_SEC = 60 * 60; // 1시간

  private AuthSessionUtil() {}

  /* ===== 조회 ===== */

  public static MemberSessionDTO getLoginUserOrNull(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    if (session == null) return null;
    Object obj = session.getAttribute(LOGIN_MEMBER_ATTR);
    return (obj instanceof MemberSessionDTO) ? (MemberSessionDTO) obj : null;
  }

  public static String getUuidOrNull(HttpServletRequest req) {
    MemberSessionDTO user = getLoginUserOrNull(req);
    return (user == null) ? null : user.getUuid();
  }

  public static boolean isLoggedIn(HttpServletRequest req) {
    return getLoginUserOrNull(req) != null;
  }

  /* ===== 가드(필수 로그인) ===== */

  public static String requireUuidOrRedirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String uuid = getUuidOrNull(req);
    if (uuid == null) {
      resp.sendRedirect(req.getContextPath() + LOGIN_PAGE_PATH);
      return null;
    }
    return uuid;
  }

  public static String requireUuidOr401(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String uuid = getUuidOrNull(req);
    if (uuid == null) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    return uuid;
  }

  /* ===== 설정/해제 ===== */

  public static void login(HttpServletRequest req, MemberSessionDTO sessionUser) {
    HttpSession session = req.getSession(true);
    session.setAttribute(LOGIN_MEMBER_ATTR, sessionUser);
    if (DEFAULT_SESSION_TIMEOUT_SEC != null) {
      session.setMaxInactiveInterval(DEFAULT_SESSION_TIMEOUT_SEC);
    }
  }

  public static void logout(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    if (session != null) session.invalidate();
  }
}
