package dto;

public class ReservationCancelDTO {
    private boolean ok;
    private String message; // UI 노출용 메시지
    private String status;  // 최종 상태(RESERVED/DONE/CANCELLED)

    public ReservationCancelDTO() {}
    public ReservationCancelDTO(boolean ok, String message, String status) {
        this.ok = ok; this.message = message; this.status = status;
    }

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
