package domain;

import java.time.LocalDateTime;

public class DoctorWeeklySchedule {
    private Long scheduleId;
    private Long doctorId;
    private int dayOfWeek; // 1=월요일, ..., 7=일요일
    private boolean amFlag; // 오전 진료 여부
    private boolean pmFlag; // 오후 진료 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DoctorWeeklySchedule() {}

    public DoctorWeeklySchedule(Long scheduleId, Long doctorId, int dayOfWeek, boolean amFlag, boolean pmFlag,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.amFlag = amFlag;
        this.pmFlag = pmFlag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public boolean isAmFlag() { return amFlag; }
    public void setAmFlag(boolean amFlag) { this.amFlag = amFlag; }
    public boolean isPmFlag() { return pmFlag; }
    public void setPmFlag(boolean pmFlag) { this.pmFlag = pmFlag; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}