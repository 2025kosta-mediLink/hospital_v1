package controller;

import service.DepartmentService;
import dto.DepartmentSelectDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/v1/reservation/departments")
public class DepartmentController extends HttpServlet {
    private DepartmentService departmentService;

    @Override
    public void init() throws ServletException {
        departmentService = new DepartmentService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String searchTerm = req.getParameter("searchTerm");
        DepartmentSelectDTO departmentSelectDTO;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            departmentSelectDTO = departmentService.searchDepartments(searchTerm);
        } else {
            departmentSelectDTO = departmentService.getAllDepartments();
        }
        req.setAttribute("departmentSelect", departmentSelectDTO);
        req.getRequestDispatcher("/WEB-INF/views/hospital/departmentSelect.jsp").forward(req, resp);
    }
}