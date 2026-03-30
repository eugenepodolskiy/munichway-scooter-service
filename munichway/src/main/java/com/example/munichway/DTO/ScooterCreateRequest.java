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

}