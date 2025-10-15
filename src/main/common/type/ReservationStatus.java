package common.type;

import java.util.Locale;

public enum ReservationStatus {
    RESERVED("예약완료", "badge-green"),
    DONE("접수완료", "badge-blue"),
    CANCELLED("취소", "badge-gray");

    private final String labelKo;
    private final String badgeClass;

    ReservationStatus(String labelKo, String badgeClass) {
        this.labelKo = labelKo;
        this.badgeClass = badgeClass;
    }

    public String getLabelKo() { return labelKo; }
    public String getBadgeClass() { return badgeClass; }

    /** UI 쿼리파라미터 → enum (null/ALL/빈문자면 null 반환 = 전체) */
    public static ReservationStatus fromUiOrNull(String ui) {
        if (ui == null || ui.isBlank() || "ALL".equalsIgnoreCase(ui)) return null;
        try {
            return ReservationStatus.valueOf(ui.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
