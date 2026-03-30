package com.example.munichway.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripResponse {
    private Long tripId;
    private String scooterModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalCost;
}
