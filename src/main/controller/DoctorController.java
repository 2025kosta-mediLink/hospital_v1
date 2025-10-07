package controller;

import dto.DoctorDTO;
import service.DoctorService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/v1/doctors") // 👉 URL 매핑: /v1/doctors로 들어오는 요청 처리
public class DoctorController extends HttpServlet {
  private DoctorService service;

  @Override
  public void init() throws ServletException {
    // 서블릿 초기화 시 1회 실행됨 → DoctorService 준비
    service = new DoctorService();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    // 1️⃣ departmentId 파라미터를 GET으로 받음 (이전 화면에서 전달됨)
    String departmentIdStr = req.getParameter("departmentId");

    // 2️⃣ 진료과 ID가 없을 경우 400 Bad Request 에러 반환
    if (departmentIdStr == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "departmentId is required");
      return;
    }

    // 3️⃣ 문자열 → Long 타입 변환
    Long departmentId = Long.parseLong(departmentIdStr);

    // 4️⃣ 해당 진료과 소속 의사 목록 조회 (Service → DAO → DB)
    List<DoctorDTO> list = service.getDoctorsByDepartment(departmentId);

    // 5️⃣ 조회된 의사 목록을 JSP로 전달
    req.setAttribute("doctors", list);

    // 6️⃣ Forward로 JSP 렌더링 (request 유지)
    req.getRequestDispatcher("/WEB-INF/views/reception/doctorList.jsp").forward(req, resp);
  }
}
