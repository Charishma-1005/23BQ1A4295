package com.affordmed.vehicle_maintenance_scheduler.dto;

import java.util.List;

public class ScheduleResponse {
    private int depotId;
    private int totalImpact;
    private List<String> selectedTaskIds;

    public ScheduleResponse(int depotId, int totalImpact, List<String> selectedTaskIds) {
        this.depotId = depotId;
        this.totalImpact = totalImpact;
        this.selectedTaskIds = selectedTaskIds;
    }

    public int getDepotId() {
        return depotId;
    }

    public int getTotalImpact() {
        return totalImpact;
    }

    public List<String> getSelectedTaskIds() {
        return selectedTaskIds;
    }
}
