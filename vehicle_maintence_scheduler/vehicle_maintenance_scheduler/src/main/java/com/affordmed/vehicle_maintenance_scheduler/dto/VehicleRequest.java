package com.affordmed.vehicle_maintenance_scheduler.dto;

import java.util.List;

import com.affordmed.vehicle_maintenance_scheduler.model.Depot;
import com.affordmed.vehicle_maintenance_scheduler.model.Vehicle;

public class VehicleRequest {

    private List<Vehicle> vehicles;
    private List<Depot> depots;

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<Depot> getDepots() {
        return depots;
    }

    public void setDepots(List<Depot> depots) {
        this.depots = depots;
    }
}