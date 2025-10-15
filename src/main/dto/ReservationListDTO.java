package dto;

import java.util.List;
import java.util.Map;

public class ReservationListDTO {
    // key: "YYYY-MM", value: 해당 월의 예약 리스트 (내림차순 정렬)
    private Map<String, List<ReservationListItemDTO>> groupedByMonth;
    private List<MonthOptionDTO> monthOptions;
    private String selectedMonth;   // null이면 전체
    private String selectedStatus;  // "ALL"/"RESERVED"/"DONE"/"CANCELLED"

    public Map<String, List<ReservationListItemDTO>> getGroupedByMonth() { return groupedByMonth; }
    public void setGroupedByMonth(Map<String, List<ReservationListItemDTO>> groupedByMonth) { this.groupedByMonth = groupedByMonth; }
    public List<MonthOptionDTO> getMonthOptions() { return monthOptions; }
    public void setMonthOptions(List<MonthOptionDTO> monthOptions) { this.monthOptions = monthOptions; }
    public String getSelectedMonth() { return selectedMonth; }
    public void setSelectedMonth(String selectedMonth) { this.selectedMonth = selectedMonth; }
    public String getSelectedStatus() { return selectedStatus; }
    public void setSelectedStatus(String selectedStatus) { this.selectedStatus = selectedStatus; }
}
