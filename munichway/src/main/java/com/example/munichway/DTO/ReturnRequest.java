package com.example.munichway.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ReturnRequest {

    @NotBlank(message = "New location must be provided")
    private String newLocation;

    @Min(value = 0, message = "Battery cannot be less than 0")
    @Max(value = 100, message = "Battery cannot be more than 100")
    private int newBatteryLevel;

    public String getNewLocation() { return newLocation; }

    public void setNewLocation(String newLocation) { this.newLocation = newLocation; }

    public int getNewBatteryLevel() { return newBatteryLevel; }

    public void setNewBatteryLevel(int newBatteryLevel) { this.newBatteryLevel = newBatteryLevel; }
}