// DepartmentController.java
package controller;

import dto.DepartmentDTO;
import service.DepartmentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/v1/departments") // 👉 /v1/departments 요청이 들어오면 이 서블릿이 실행됨
public class DepartmentController extends HttpServlet {
  private DepartmentService service; // 비즈니스 로직 담당 서비스 객체

  @Override
  public void init() throws ServletException {
    // 서블릿 초기화 시 1회 실행 (DAO와 연결된 Service 인스턴스 준비)
    service = new DepartmentService();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    // 1️⃣ 진료과 전체 목록 조회 (DAO → DB)
    List<DepartmentDTO> list = service.getDepartments();

    // 2️⃣ 조회한 결과를 request 객체에 담아서 JSP로 전달
    req.setAttribute("departments", list);

    // 3️⃣ Forward 방식으로 JSP 렌더링 (요청/응답이 이어짐)
    req.getRequestDispatcher("/WEB-INF/views/reception/departmentList.jsp")
            .forward(req, resp);
  }
}
