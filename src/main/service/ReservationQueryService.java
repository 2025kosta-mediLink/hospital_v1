// src/main/java/service/ReservationQueryService.java
package service;

import dao.ReservationQueryDAO;
import dto.ReservationDetailDTO;
import dto.ReservationSummaryDTO;

import java.util.List;

/** 단순 위임 서비스 (인터페이스/DI 없이) */
public class ReservationQueryService {

  private final ReservationQueryDAO dao;

  public ReservationQueryService() {
    this.dao = new ReservationQueryDAO(); // DriverManager 버전 DAO
  }
  public ReservationQueryService(ReservationQueryDAO dao){
    this.dao = dao;
  }

  public boolean existsTodayByMemberUuid(String memberUuid){
    return dao.countTodayByMemberUuid(memberUuid) > 0;
  }

  public List<ReservationSummaryDTO> findTodayByMemberUuid(String memberUuid){
    return dao.findTodayByMemberUuid(memberUuid);
  }

  public ReservationDetailDTO getDetailForReception(String memberUuid, long reservationId){
    return dao.findDetailForReception(memberUuid, reservationId);
  }
}
