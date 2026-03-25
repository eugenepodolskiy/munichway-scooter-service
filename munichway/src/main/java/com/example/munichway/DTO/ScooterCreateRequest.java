package com.example.munichway.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScooterCreateRequest {

    @NotBlank(message = "Model name cannot be empty")
    private String modelName;

    @NotBlank(message = "Location cannot be empty")
    private String location;

    @NotNull(message = "Battery level cannot be null")
    private Integer batteryLevel;

    public @NotBlank(message = "Model name cannot be empty") String getModelName() {
        return modelName;
    }

    public void setModelName(@NotBlank(message = "Model name cannot be empty") String modelName) {
        this.modelName = modelName;
    }

    public @NotBlank(message = "Location cannot be empty") String getLocation() {
        return location;
    }

    public void setLocation(@NotBlank(message = "Location cannot be empty") String location) {
        this.location = location;
    }

    public @NotNull(message = "Battery level cannot be null") Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(@NotNull(message = "Battery level cannot be null") Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}