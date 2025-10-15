package controller;

import common.util.AuthSessionUtil;
import dto.ReservationListDTO;
import service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/v1/reservation/list"})
public class ReservationListController extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        reservationService = new ReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
        if (uuid == null) return;

        String month  = req.getParameter("month");   // "YYYY-MM" or null
        String status = req.getParameter("status");  // "ALL|RESERVED|DONE|CANCELLED" or null

        ReservationListDTO result =
                reservationService.getReservationList(uuid, month, status);

        req.setAttribute("grouped", result.getGroupedByMonth());
        req.setAttribute("monthOptions", result.getMonthOptions());
        req.setAttribute("selectedMonth", result.getSelectedMonth());
        req.setAttribute("selectedStatus", result.getSelectedStatus());
        req.getRequestDispatcher("/WEB-INF/views/reservation/reservationList.jsp").forward(req, resp);
    }
}
