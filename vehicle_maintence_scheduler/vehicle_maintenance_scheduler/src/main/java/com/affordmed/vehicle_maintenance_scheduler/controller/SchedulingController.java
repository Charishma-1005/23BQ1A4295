package com.affordmed.vehicle_maintenance_scheduler.controller;

import com.affordmed.vehicle_maintenance_scheduler.dto.ScheduleResponse;
import com.affordmed.vehicle_maintenance_scheduler.service.SchedulingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @GetMapping("/schedule")
    public List<ScheduleResponse> getSchedule() {
        return schedulingService.runScheduler();
    }
}
