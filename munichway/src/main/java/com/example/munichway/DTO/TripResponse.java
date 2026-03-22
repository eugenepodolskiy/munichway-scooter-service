package com.example.munichway.DTO;

import java.time.LocalDateTime;

public class TripResponse {
    private Long tripId;
    private String scooterModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalCost;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getScooterModel() {
        return scooterModel;
    }

    public void setScooterModel(String scooterModel) {
        this.scooterModel = scooterModel;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}
