package controller;

import dto.DepartmentListItemDTO;
import dto.DoctorListItemDTO;
import dto.ReceptionDetailDTO;
import dto.SymptomListItemDTO;
import service.*;

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
    private DepartmentService departmentService;
    private DoctorService doctorService;
    private ReceptionListService receptionListService;

    @Override
    public void init() throws ServletException {
        symptomService = new SymptomService();
        receptionService = new ReceptionService();
        departmentService = new DepartmentService();
        doctorService = new DoctorService();
        receptionListService = new ReceptionListService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo(); // 예: "/symptom", "/done"

        if (path == null || path.equals("/symptom")) {
            // 증상 선택 화면
            req.setAttribute("symptoms", symptomService.getSymptoms());
            req.setAttribute("departmentId", req.getParameter("departmentId"));
            req.setAttribute("doctorId", req.getParameter("doctorId"));

            req.getRequestDispatcher("/WEB-INF/views/reception/receptionSymptom.jsp").forward(req, resp);

        } else if (path.equals("/departments")) {
            // 1. 진료과 전체 목록 조회 (DAO → DB)
            List<DepartmentListItemDTO> list = departmentService.getDepartments();

            // 2. 조회한 결과를 request 객체에 담아서 JSP로 전달
            req.setAttribute("departments", list);

            // 3. Forward 방식으로 JSP 렌더링 (요청/응답이 이어짐)
            req.getRequestDispatcher("/WEB-INF/views/reception/departmentList.jsp")
                    .forward(req, resp);

        } else if (path.equals("/doctors")) {

            // 1. departmentId 파라미터를 GET으로 받음 (이전 화면에서 전달됨)
            String departmentIdStr = req.getParameter("departmentId");

            // 2. 진료과 ID가 없을 경우 400 Bad Request 에러 반환
            if (departmentIdStr == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "departmentId is required");
                return;
            }

            // 3. 문자열 → Long 타입 변환
            Long departmentId = Long.parseLong(departmentIdStr);

            // 4. 해당 진료과 소속 의사 목록 조회 (Service → DAO → DB)
            List<DoctorListItemDTO> list = doctorService.getDoctorsByDepartment(departmentId);

            // 5. 조회된 의사 목록을 JSP로 전달
            req.setAttribute("doctors", list);

            // 6. Forward로 JSP 렌더링 (request 유지)
            req.getRequestDispatcher("/WEB-INF/views/reception/doctorList.jsp").forward(req, resp);

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

          Long memberId = 1L; // (임시) 로그인 붙으면 세션에서 가져오기
          String reason = req.getParameter("reason");

          boolean ok;
          String msg;
          try {
            ReceptionListService.CancelResult r =
                receptionListService.cancelReceptionTransactional(receptionId, memberId, reason);
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
            String departmentId = req.getParameter("departmentId");
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

            // 이름 표시용 데이터 (선택)
            String departmentName = departmentService.findNameById(Long.parseLong(departmentId));
            String doctorName = doctorService.findNameById(Long.parseLong(doctorId));

            req.setAttribute("departmentId", departmentId);
            req.setAttribute("doctorId", doctorId);
            req.setAttribute("symptomIds", symptomIds);
            req.setAttribute("symptomNames", selectedNames);
            req.setAttribute("noteToDoctor", noteToDoctor);
            req.setAttribute("consentNotice", consentNotice);
            req.setAttribute("departmentName", departmentName);
            req.setAttribute("doctorName", doctorName);

            req.getRequestDispatcher("/WEB-INF/views/reception/receptionConfirm.jsp").forward(req, resp);

        } else if ("/done".equals(path)) {
            req.setCharacterEncoding("UTF-8");

//            Long departmentId = Long.parseLong(req.getParameter("departmentId"));
            Long doctorId = Long.parseLong(req.getParameter("doctorId"));
            String[] symptomIds = req.getParameterValues("symptomIds");
            String noteToDoctor = req.getParameter("noteToDoctor");
            boolean consentNotice = "true".equals(req.getParameter("consentNotice"));

            // (임시) 회원 기능이 없으므로 고정 memberId 사용
            // 나중에 로그인 기능이 완성되면 세션에서 꺼내는 코드로 교체하면 됨
            Long memberId = 1L;

            // 서버 측 검증
            if (!consentNotice) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "consentNotice required");
                return;
            }

            // 접수 생성
            Long receptionId = receptionService.createReception(
                    memberId,
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
