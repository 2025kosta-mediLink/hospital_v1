package controller;

import dto.*;
import service.*;
import common.util.AuthSessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/v1/reception/*")
public class ReceptionController extends HttpServlet {
    private SymptomService symptomService;
    private ReceptionService receptionService;
    private DoctorService doctorService;
    private ReceptionListService receptionListService;

    @Override
    public void init() throws ServletException {
        symptomService = new SymptomService();
        receptionService = new ReceptionService();
        doctorService = new DoctorService();
        receptionListService = new ReceptionListService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo(); // 예: "/symptom", "/done"

        if (path == null || path.equals("/symptom")) {
            // 증상 선택 화면
            String doctorId = req.getParameter("doctorId");
            Long departmentId = doctorService.getDepartmentIdByDoctorId(Long.parseLong(doctorId));

            req.setAttribute("symptoms", symptomService.getSymptoms());
            req.setAttribute("departmentId", departmentId);
            req.setAttribute("doctorId", doctorId);

            req.getRequestDispatcher("/WEB-INF/views/reception/receptionSymptom.jsp").forward(req, resp);

        } else if (path.equals("/done")) {
            req.setCharacterEncoding("UTF-8");

            // 1. 쿼리 파라미터 확인
            String receptionIdParam = req.getParameter("receptionId");
            if (receptionIdParam == null || receptionIdParam.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing receptionId parameter");
                return;
            }

            Long receptionId;
            try {
                receptionId = Long.parseLong(receptionIdParam);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid receptionId format");
                return;
            }

            // 2. DB에서 접수 상세 조회
            ReceptionDetailDTO detail = receptionService.getReceptionDetail(receptionId);

            // 3. 존재하지 않으면 404 처리
            if (detail == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Reception not found");
                return;
            }

            // 4. JSP에 데이터 전달
            req.setAttribute("reception", detail);

            // 5. JSP forward
            req.getRequestDispatcher("/WEB-INF/views/reception/receptionDone.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지

        String path = req.getPathInfo();

        // 1) 취소 라우트 우선 처리: "/{id}/cancel"
        if (path != null) {
            Matcher m = Pattern.compile("^/(\\d+)/cancel$").matcher(path);
            if (m.matches()) {
                Long receptionId;
                try {
                    receptionId = Long.parseLong(m.group(1));
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid reception id");
                    return;
                }

                // 세션에서 uuid 확보 (미로그인 시 401)
                String uuid = AuthSessionUtil.requireUuidOr401(req, resp);
                if (uuid == null) return;

                String reason = req.getParameter("reason");

                boolean ok;
                String msg;
                try {
                    // memberId 대신 uuid 사용
                    ReceptionListService.CancelResult r =
                            receptionListService.cancelReceptionTransactional(receptionId, uuid, reason);
                    ok = r.isSuccess();
                    msg = (r.getMessage() != null && !r.getMessage().isBlank())
                            ? r.getMessage()
                            : (ok ? "접수가 취소되었습니다." : "취소에 실패했습니다.");
                } catch (Exception e) {
                    e.printStackTrace();
                    ok = false;
                    msg = "서버 오류로 취소 처리에 실패했습니다.";
                }

                String redirect = req.getContextPath()
                        + "/v1/reception/detail?id=" + receptionId
                        + "&cancel=" + (ok ? "1" : "0")
                        + "&msg=" + java.net.URLEncoder.encode(msg, "UTF-8");
                resp.sendRedirect(redirect);
                return;
            }
        }

        // [확인 화면 이동]
        if ("/confirm".equals(path)) {
            String doctorId = req.getParameter("doctorId");
            String[] symptomIds = req.getParameterValues("symptomIds");
            String noteToDoctor = req.getParameter("noteToDoctor");
            boolean consentNotice = "true".equals(req.getParameter("consentNotice"));

            // 선택 증상명 조회
            List<String> selectedNames = new ArrayList<>();
            for (SymptomListItemDTO s : symptomService.getSymptoms()) {
                if (symptomIds != null) {
                    for (String sid : symptomIds) {
                        if (s.getSymptomId().equals(Long.parseLong(sid))) {
                            selectedNames.add(s.getName());
                        }
                    }
                }
            }

            DoctorDetailDTO doctorDetailDTO = doctorService.getDoctorById(Long.parseLong(doctorId));

            req.setAttribute("doctorId", doctorId);
            req.setAttribute("symptomIds", symptomIds);
            req.setAttribute("symptomNames", selectedNames);
            req.setAttribute("noteToDoctor", noteToDoctor);
            req.setAttribute("consentNotice", consentNotice);
            req.setAttribute("departmentName", doctorDetailDTO.getDepartmentName());
            req.setAttribute("doctorName", doctorDetailDTO.getName());

            req.getRequestDispatcher("/WEB-INF/views/reception/receptionConfirm.jsp").forward(req, resp);

        } else if ("/done".equals(path)) {
            req.setCharacterEncoding("UTF-8");

            Long doctorId = Long.parseLong(req.getParameter("doctorId"));
            String[] symptomIds = req.getParameterValues("symptomIds");
            String noteToDoctor = req.getParameter("noteToDoctor");
            boolean consentNotice = "true".equals(req.getParameter("consentNotice"));

            // 세션에서 uuid 확보 (미로그인 시 401)
            String uuid = AuthSessionUtil.requireUuidOr401(req, resp);
            if (uuid == null) return;

            // 서버 측 검증
            if (!consentNotice) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "consentNotice required");
                return;
            }

            // 접수 생성 (uuid 사용)
            Long receptionId = receptionService.createReception(
                    uuid,
                    doctorId,
                    symptomIds,
                    noteToDoctor,
                    consentNotice
            );

            if (receptionId == null) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reception creation failed");
                return;
            }

            // 완료 페이지로 리다이렉트 (receptionId 전달)
            resp.sendRedirect(req.getContextPath() + "/v1/reception/done?receptionId=" + receptionId);
        }
    }
}
