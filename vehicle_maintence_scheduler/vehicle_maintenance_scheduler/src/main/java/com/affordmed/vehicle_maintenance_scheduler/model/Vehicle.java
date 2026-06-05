package com.affordmed.vehicle_maintenance_scheduler.model;

public class Vehicle {
    private String TaskID;
    private int Duration;
    private int Impact;

    public String getTaskID() {
        return TaskID;
    }

    public void setTaskID(String taskID) {
        TaskID = taskID;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getImpact() {
        return Impact;
    }

    public void setImpact(int impact) {
        Impact = impact;
    }
}
