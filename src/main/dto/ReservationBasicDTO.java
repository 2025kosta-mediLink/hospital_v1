package dto;

public class ReservationBasicDTO {
    private Long reservationId;
    private Long memberId;
    private String status; // RESERVED / DONE / CANCELLED

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
