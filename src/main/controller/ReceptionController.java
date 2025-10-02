package controller;

import dto.SymptomDTO;
import service.DepartmentService;
import service.SymptomService;
import service.ReceptionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/v1/reception/*")
public class ReceptionController extends HttpServlet {
  private SymptomService symptomService;
  private ReceptionService receptionService;
  private DepartmentService departmentService;

  @Override
  public void init() throws ServletException {
    symptomService = new SymptomService();
    receptionService = new ReceptionService();
    departmentService = new DepartmentService();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String path = req.getPathInfo(); // /symptom, /confirm, /done
    System.out.println("symptoms size = " + symptomService.getSymptoms().size());

    if (path == null || path.equals("/symptom")) {
      // 증상 선택 화면
      req.setAttribute("symptoms", symptomService.getSymptoms());
      req.setAttribute("departmentId", req.getParameter("departmentId"));
      req.setAttribute("doctorId", req.getParameter("doctorId"));
      req.getRequestDispatcher("/WEB-INF/views/reception/receptionSymptom.jsp").forward(req, resp);

    } else if (path.equals("/done")) {
      // 완료 화면
      String receptionId = req.getParameter("receptionId");
      req.setAttribute("receptionId", receptionId);
      req.getRequestDispatcher("/WEB-INF/views/reception/receptionDone.jsp").forward(req, resp);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String path = req.getPathInfo();

    if ("/confirm".equals(path)) {
      // 증상 입력 후 → 확인 화면
      String departmentId = req.getParameter("departmentId");
//      String departmentName = departmentService.findNameById(Long.parseLong(departmentId));

      String doctorId = req.getParameter("doctorId");
      String[] symptomIds = req.getParameterValues("symptomIds");
      String noteToDoctor = req.getParameter("noteToDoctor");
      boolean consentNotice = "true".equals(req.getParameter("consentNotice"));

      // 증상명 조회
      List<SymptomDTO> allSymptoms = symptomService.getSymptoms();
      List<String> selectedNames = new ArrayList<>();
      if (symptomIds != null) {
        for (String idStr : symptomIds) {
          Long id = Long.parseLong(idStr);
          allSymptoms.stream()
              .filter(s -> s.getSymptomId().equals(id))
              .findFirst()
              .ifPresent(s -> selectedNames.add(s.getName()));
        }
      }

      req.setAttribute("departmentId", departmentId);
      req.setAttribute("doctorId", doctorId);
      req.setAttribute("symptomIds", symptomIds);
      req.setAttribute("symptomNames", selectedNames);
      req.setAttribute("noteToDoctor", noteToDoctor);
      req.setAttribute("consentNotice", consentNotice);
      req.setAttribute("departmentName", departmentName);

      req.getRequestDispatcher("/WEB-INF/views/reception/receptionConfirm.jsp").forward(req, resp);

    } else if ("/done".equals(path)) {
      // 최종 접수 DB 저장
      Long departmentId = Long.parseLong(req.getParameter("departmentId"));
      Long doctorId = Long.parseLong(req.getParameter("doctorId"));
      String[] symptomIds = req.getParameterValues("symptomIds");
      String noteToDoctor = req.getParameter("noteToDoctor");
      boolean consentNotice = "true".equals(req.getParameter("consentNotice"));

      Long receptionId = receptionService.createReception(
          departmentId, doctorId, symptomIds, noteToDoctor, consentNotice);

      resp.sendRedirect(req.getContextPath() + "/v1/reception/done?receptionId=" + receptionId);
    }
  }
}
