package com.affordmed.vehicle_maintenance_scheduler.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        List<ScheduleResponse> responses = new ArrayList<>();

        try {
            Depot[] depots = fetchDepots();
            Vehicle[] vehicles = fetchVehicles();

            if (depots == null || vehicles == null) {
                System.err.println("❌ API returned null data.");
                return responses;
            }

            for (Depot depot : depots) {
                ScheduleResponse result = scheduleForDepot(depot, vehicles);
                responses.add(result);
            }
        } catch (Exception e) {
            System.err.println("❌ Error running scheduler: " + e.getMessage());
        }

        return responses;
    }

    private Depot[] fetchDepots() {
        ResponseEntity<Depot[]> response = restTemplate.exchange(
            DEPOT_API, HttpMethod.GET, buildAuthEntity(), Depot[].class
        );
        return response.getBody();
    }

    private Vehicle[] fetchVehicles() {
        ResponseEntity<Vehicle[]> response = restTemplate.exchange(
            VEHICLE_API, HttpMethod.GET, buildAuthEntity(), Vehicle[].class
        );
        return response.getBody();
    }

    private HttpEntity<String> buildAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiJtYWRkaW5lbmljaGFyaXNobWFAZ21haWwuY29tIiwiZXhwIjoxNzgwNjQxNTI5LCJpYXQiOjE3ODA2NDA2MjksImlzcyI6IkFmZm9yZCBNZWRpY2FsIFRlY2hub2xvZ2llcyBQcml2YXRlIExpbWl0ZWQiLCJqdGkiOiJiZjhiM2VlOC1lNTY3LTQyYjEtOTk5MC1kZDZjYmViZTc0OTkiLCJsb2NhbGUiOiJlbi1JTiIsIm5hbWUiOiJtLiBjaGFyaXNobWEiLCJzdWIiOiJlZDAzYTM0MC1hMzZjLTRkYzktOGZjZS0xZjZhOTZkYTZiN2UifSwiZW1haWwiOiJtYWRkaW5lbmljaGFyaXNobWFAZ21haWwuY29tIiwibmFtZSI6Im0uIGNoYXJpc2htYSIsInJvbGxObyI6IjIzYnExYTQyOTUiLCJhY2Nlc3NDb2RlIjoiUVFkRVl5IiwiY2xpZW50SUQiOiJlZDAzYTM0MC1hMzZjLTRkYzktOGZjZS0xZjZhOTZkYTZiN2UiLCJjbGllbnRTZWNyZXQiOiJ5dEJDbmdGS3NYV1JkQ2JiIn0.XA8h9v7S3ZK5f58ctEV_c2S-hL7qR-y9wxeK2Trpjrg"); // Replace with actual token
        return new HttpEntity<>(headers);
    }

    private ScheduleResponse scheduleForDepot(Depot depot, Vehicle[] vehicles) {
        int n = vehicles.length;
        int maxHours = depot.getMechanicHours();
        int[][] dp = new int[n + 1][maxHours + 1];

        // Dynamic programming knapsack
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

        // Backtrack to find selected tasks
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
