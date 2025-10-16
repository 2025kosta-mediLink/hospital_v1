package controller;

import service.DoctorService;
import service.DoctorNoticeService;
import service.DoctorWeeklyScheduleService;
import dto.DoctorSelectDTO;
import common.util.AuthSessionUtil;  // AuthSessionUtil 임포트

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/v1/reservation/doctors", "/v1/reception/doctors"})
public class DoctorController extends HttpServlet {
    private DoctorService doctorService;
    private DoctorNoticeService doctorNoticeService;
    private DoctorWeeklyScheduleService doctorWeeklyScheduleService;

    @Override
    public void init() throws ServletException {
        doctorService = new DoctorService();
        doctorNoticeService = new DoctorNoticeService();
        doctorWeeklyScheduleService = new DoctorWeeklyScheduleService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
        if (uuid == null) {
            return;
        }

        Long departmentId = Long.parseLong(req.getParameter("departmentId"));
        List<DoctorSelectDTO> doctorList = doctorService.getDoctorsByDepartment(departmentId); // 의사 목록 조회

        // doctorList에 있는 모든 의사에 대해 공지사항과 일정 갱신
        for (DoctorSelectDTO dto : doctorList) {
            // 해당 의사의 공지사항을 가져와서 추가
            dto.setNotices(doctorNoticeService.getNoticesForDoctor(dto.getId()));

            // 해당 의사의 주간 진료 일정을 가져와서 추가
            dto.setSchedule(doctorWeeklyScheduleService.getWeeklyScheduleForDoctor(dto.getId()));
        }

        req.setAttribute("doctorList", doctorList);
        req.getRequestDispatcher("/WEB-INF/views/hospital/doctorSelect.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 로그인 체크
        String uuid = AuthSessionUtil.requireUuidOrRedirect(req, resp);
        if (uuid == null) {
            return;
        }

        String servletPath = req.getServletPath();
        String doctorId = req.getParameter("doctorId");

        String ctx = req.getContextPath();
        if ("/v1/reservation/doctors".equals(servletPath)) {
            resp.sendRedirect(ctx + "/v1/reservation/date-selection?doctorId=" + java.net.URLEncoder.encode(doctorId, "UTF-8"));
        } else if ("/v1/reception/doctors".equals(servletPath)) {
            resp.sendRedirect(ctx + "/v1/reception/confirm?doctorId=" + java.net.URLEncoder.encode(doctorId, "UTF-8"));
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
