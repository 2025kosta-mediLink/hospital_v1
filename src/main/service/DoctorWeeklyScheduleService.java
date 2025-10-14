package service;

import dao.DoctorWeeklyScheduleDAO;
import domain.DoctorWeeklySchedule; // 수정된 도메인 클래스
import dto.ScheduleDetailDTO;

import java.util.List;

public class DoctorWeeklyScheduleService {
    private DoctorWeeklyScheduleDAO doctorWeeklyScheduleDAO;

    public DoctorWeeklyScheduleService() {
        doctorWeeklyScheduleDAO = new DoctorWeeklyScheduleDAO();
    }

    public List<ScheduleDetailDTO> getWeeklyScheduleForDoctor(Long doctorId) {
        return doctorWeeklyScheduleDAO.findScheduleByDoctorId(doctorId);
    }
}
