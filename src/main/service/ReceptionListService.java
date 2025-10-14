package service;

import dao.ReceptionListDAO;
import dto.ReceptionListDetailDTO;
import dto.ReceptionListItemDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 목록/상세 + 취소(Transaction 위임) 서비스
 *
 * - getList(), getReceptionListDetail(): 기존 목록/상세
 * - cancelReceptionTransactional(): 취소 트랜잭션을 DAO에 위임, 결과만 캡슐화해서 반환
 */
public class ReceptionListService {

  private final ReceptionListDAO dao = new ReceptionListDAO();

  /* ================= 기존 목록/상세 그대로 ================= */
  public List<ReceptionListItemDTO> getList(Long memberId, String status, LocalDate from, LocalDate to) {
    return dao.findList(memberId, status, from, to);
  }

  public ReceptionListDetailDTO getReceptionListDetail(Long receptionId) {
    return dao.getReceptionListDetail(receptionId);
  }
  /* ====================================================== */

  /**
   * 접수 취소 트랜잭션 실행
   * - 컨트롤러에서 URL/파라미터만 검증하고, 핵심 규칙/트랜잭션은 DAO에서 보장합니다.
   * - 이 메서드는 "결과(성공/실패, 메시지, 취소 가능 여부)"만 캡슐화해서 컨트롤러에 전달합니다.
   */
  public CancelResult cancelReceptionTransactional(Long receptionId, Long memberId, String reason) {
    try {
      return dao.cancelReceptionTransactional(receptionId, memberId, reason);
    } catch (Exception e) {
      e.printStackTrace();
      // 예외는 여기서 한 번 캡슐화. 컨트롤러에는 안전한 메시지만 노출.
      return new CancelResult(false, true, "서버 오류로 취소 처리에 실패했습니다.");
    }
  }

  /** 컨트롤러로 돌려줄 간단한 결과 DTO(내부 클래스) */
  public static class CancelResult {
    private final boolean success;      // 트랜잭션 성공 여부
    private final boolean cancellable;  // 논리적으로 취소 가능한 상황이었는지 여부(검증 결과)
    private final String message;       // 사용자에게 보여줄 메시지

    public CancelResult(boolean success, boolean cancellable, String message) {
      this.success = success;
      this.cancellable = cancellable;
      this.message = message;
    }

    public boolean isSuccess() { return success; }
    public boolean isCancellable() { return cancellable; }
    public String getMessage() { return message; }
  }
}
