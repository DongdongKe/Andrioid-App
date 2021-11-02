package com.example.android_mobile_app.domain;

public class Event {
    private String name;
    private Boolean isRunning;
    private int startTime;
    private int endTime;

    public Event(String name) {
        this.name = name;
        this.isRunning = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return isRunning;
    }

    public void setStatus(Boolean runningStatus) {
        isRunning = runningStatus;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
