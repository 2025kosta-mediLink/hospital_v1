package dto;

public class ScheduleDetailDTO {
    private int dayOfWeek;  // 요일 (1=월요일, 2=화요일, ..., 7=일요일)
    private boolean amFlag;  // 오전 진료 여부
    private boolean pmFlag;  // 오후 진료 여부

    public ScheduleDetailDTO(int dayOfWeek, boolean amFlag, boolean pmFlag) {
        this.dayOfWeek = dayOfWeek;
        this.amFlag = amFlag;
        this.pmFlag = pmFlag;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isAmFlag() {
        return amFlag;
    }

    public void setAmFlag(boolean amFlag) {
        this.amFlag = amFlag;
    }

    public boolean isPmFlag() {
        return pmFlag;
    }

    public void setPmFlag(boolean pmFlag) {
        this.pmFlag = pmFlag;
    }
}
