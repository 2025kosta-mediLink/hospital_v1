package service;

import dao.DoctorNoticeDAO;
import dto.NoticeDetailDTO;
import java.util.List;

public class DoctorNoticeService {
    private DoctorNoticeDAO doctorNoticeDAO;

    public DoctorNoticeService() {
        doctorNoticeDAO = new DoctorNoticeDAO();
    }

    public List<NoticeDetailDTO> getNoticesForDoctor(Long doctorId) {
        return doctorNoticeDAO.findNoticesByDoctorId(doctorId);
    }
}