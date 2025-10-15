package controller;

import common.util.AuthSessionUtil;
import dto.DepartmentSelectDTO;
import service.DepartmentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/v1/reservation/departments", "/v1/reception/departments"})
public class DepartmentController extends HttpServlet {

    private DepartmentService departmentService;

    @Override
    public void init() throws ServletException {
        departmentService = new DepartmentService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
        if (uuid == null) {
            return;
        }

        String searchTerm = req.getParameter("searchTerm");
        DepartmentSelectDTO departmentSelectDTO;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            departmentSelectDTO = departmentService.searchDepartments(searchTerm);
        } else {
            departmentSelectDTO = departmentService.getAllDepartments();
        }

        req.setAttribute("departmentSelect", departmentSelectDTO);
        req.setAttribute("servletPath", req.getServletPath());

        req.getRequestDispatcher("/WEB-INF/views/hospital/departmentSelect.jsp").forward(req, resp);
    }
}
