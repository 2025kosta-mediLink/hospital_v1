package dto;

import java.sql.Time;

public class DoctorWeeklyScheduleDTO {
    private String dayOfWeek;  // 요일 (MON, TUE, WED, THU, FRI, SAT, SUN)
    private Time startTime;    // 근무 시작 시간
    private Time endTime;      // 근무 종료 시간

    public DoctorWeeklyScheduleDTO(String dayOfWeek, Time startTime, Time endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
