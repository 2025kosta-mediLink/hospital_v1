package controller;

import dto.ReceptionListItemDTO;
import service.ReceptionListService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@WebServlet("/v1/reception/list")
public class ReceptionListController extends HttpServlet {
    private ReceptionListService service;

    @Override
    public void init() throws ServletException {
        service = new ReceptionListService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // (임시) 로그인 미구현 → 고정 memberId
        Long memberId = 1L;

        // ====== 쿼리 파라미터 수집 ======
        // 상태: 기본값은 ALL
        String status = nv(req.getParameter("status"), "ALL");

        // 기간(두 방식 중 하나 사용)
        // 1) month=YYYY-MM 이 오면 해당 월 전체를 기간으로 자동 지정
        String month = req.getParameter("month");

        // 2) from/to=YYYY-MM-DD 가 오면 그대로 사용 (month 없을 때)
        String fromStr = req.getParameter("from");
        String toStr   = req.getParameter("to");

        LocalDate from = null;
        LocalDate to   = null;

        // ----- 1) month 우선 처리 -----
        if (month != null && !month.isEmpty()) {
            // 예: month=2024-01 → from=2024-01-01, to=2024-01-31
            YearMonth ym = parseYearMonthOrNull(month);
            if (ym != null) {
                from = ym.atDay(1);
                to   = ym.atEndOfMonth();
            }
        }

        // ----- 2) month가 없거나 파싱 실패 시 from/to 사용 -----
        if (from == null || to == null) {
            from = parseDateOrNull(fromStr); // yyyy-MM-dd
            to   = parseDateOrNull(toStr);   // yyyy-MM-dd
        }

        // ====== 서비스 조회 ======
        List<ReceptionListItemDTO> list = service.getList(memberId, status, from, to);

        // ====== 뷰 바인딩 ======
        req.setAttribute("receptions", list);
        req.setAttribute("status", status);
        // 화면에서 현재 선택값 유지용 (pill UI)
        req.setAttribute("month", month);
        req.setAttribute("from", fromStr);
        req.setAttribute("to", toStr);

        // ====== 뷰 이동 ======
        req.getRequestDispatcher("/WEB-INF/views/reception/receptionList.jsp")
                .forward(req, resp);
    }

    // ---------- helpers ----------
    /** null/빈 문자열이면 기본값 반환 */
    private String nv(String v, String def) {
        return (v == null || v.isEmpty()) ? def : v;
    }

    /** yyyy-MM-dd → LocalDate, 실패 시 null */
    private LocalDate parseDateOrNull(String s) {
        try { return (s == null || s.isEmpty()) ? null : LocalDate.parse(s); }
        catch (Exception e) { return null; }
    }

    /** yyyy-MM → YearMonth, 실패 시 null */
    private YearMonth parseYearMonthOrNull(String s) {
        try { return (s == null || s.isEmpty()) ? null : YearMonth.parse(s); }
        catch (Exception e) { return null; }
    }
}
