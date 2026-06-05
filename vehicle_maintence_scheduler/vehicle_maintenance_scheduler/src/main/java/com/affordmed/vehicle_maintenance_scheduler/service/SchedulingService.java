package com.affordmed.vehicle_maintenance_scheduler.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.affordmed.vehicle_maintenance_scheduler.dto.ScheduleResponse;
import com.affordmed.vehicle_maintenance_scheduler.model.Depot;
import com.affordmed.vehicle_maintenance_scheduler.model.Vehicle;

@Service
public class SchedulingService {

    private final RestTemplate restTemplate;

    private static final String DEPOT_API = "http://4.224.186.213/evaluation-service/depots";
    private static final String VEHICLE_API = "http://4.224.186.213/evaluation-service/vehicles";

    public SchedulingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ScheduleResponse> runScheduler() {
        Depot[] depots = restTemplate.getForObject(DEPOT_API, Depot[].class);
        Vehicle[] vehicles = restTemplate.getForObject(VEHICLE_API, Vehicle[].class);

        List<ScheduleResponse> responses = new ArrayList<>();

        if (depots == null || vehicles == null) return responses;

        for (Depot depot : depots) {
            ScheduleResponse result = scheduleForDepot(depot, vehicles);
            responses.add(result);
        }

        return responses;
    }

    private ScheduleResponse scheduleForDepot(Depot depot, Vehicle[] vehicles) {
        int n = vehicles.length;
        int maxHours = depot.getMechanicHours();
        int[][] dp = new int[n + 1][maxHours + 1];

        for (int i = 1; i <= n; i++) {
            int duration = vehicles[i - 1].getDuration();
            int impact = vehicles[i - 1].getImpact();
            for (int h = 1; h <= maxHours; h++) {
                if (duration <= h) {
                    dp[i][h] = Math.max(impact + dp[i - 1][h - duration], dp[i - 1][h]);
                } else {
                    dp[i][h] = dp[i - 1][h];
                }
            }
        }

        List<String> selectedTasks = new ArrayList<>();
        int h = maxHours;
        for (int i = n; i > 0; i--) {
            if (dp[i][h] != dp[i - 1][h]) {
                selectedTasks.add(vehicles[i - 1].getTaskID());
                h -= vehicles[i - 1].getDuration();
            }
        }

        return new ScheduleResponse(depot.getID(), dp[n][maxHours], selectedTasks);
    }
}
