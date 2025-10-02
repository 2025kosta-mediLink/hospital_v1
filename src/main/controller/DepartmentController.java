// DepartmentController.java
package controller;

import dto.DepartmentDTO;
import service.DepartmentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/v1/departments")
public class DepartmentController extends HttpServlet {
  private DepartmentService service;

  @Override
  public void init() throws ServletException {
    service = new DepartmentService();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<DepartmentDTO> list = service.getDepartments();
    req.setAttribute("departments", list);
    req.getRequestDispatcher("/WEB-INF/views/reception/departmentList.jsp").forward(req, resp);
  }
}
