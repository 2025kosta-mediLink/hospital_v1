package service;

import dao.ReceptionListDAO;
import dto.ReceptionListDetailDTO;
import dto.ReceptionListItemDTO;

import java.time.LocalDate;
import java.util.List;

public class ReceptionListService {
  private final ReceptionListDAO dao = new ReceptionListDAO();

  public List<ReceptionListItemDTO> getList(Long memberId,
                                            String status,
                                            LocalDate from,
                                            LocalDate to) {
    return dao.findList(memberId, status, from, to);
  }

  public ReceptionListDetailDTO getReceptionListDetail(Long receptionId) {
    return dao.getReceptionListDetail(receptionId);
  }

  public boolean cancelReception(Long receptionId) {
    return dao.cancelReception(receptionId);
  }
}
