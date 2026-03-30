package com.example.munichway.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReturnRequest {

    @NotBlank(message = "New location must be provided")
    private String newLocation;

    @Min(value = 0, message = "Battery cannot be less than 0")
    @Max(value = 100, message = "Battery cannot be more than 100")
    private int newBatteryLevel;

}