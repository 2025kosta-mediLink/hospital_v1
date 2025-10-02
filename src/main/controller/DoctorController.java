// DoctorController.java
package controller;

import dto.DoctorDTO;
import service.DoctorService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/v1/doctors")
public class DoctorController extends HttpServlet {
  private DoctorService service;

  @Override
  public void init() throws ServletException {
    service = new DoctorService();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String departmentIdStr = req.getParameter("departmentId");
    if (departmentIdStr == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "departmentId is required");
      return;
    }

    Long departmentId = Long.parseLong(departmentIdStr);
    List<DoctorDTO> list = service.getDoctorsByDepartment(departmentId);

    req.setAttribute("doctors", list);
    req.getRequestDispatcher("/WEB-INF/views/reception/doctorList.jsp").forward(req, resp);
  }
}
