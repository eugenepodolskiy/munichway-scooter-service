package com.example.munichway.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Scooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Model name is required")
    private String modelName;

    @Min(value = 0, message = "Battery level cannot be less than 0")
    @Max(value = 100, message = "Battery level cannot be more than 100")
    private Integer batteryLevel;

    @Column(name = "is_available")
    private boolean available;

    @NotBlank(message = "Location is required")
    private String location;

    public Scooter() {
    }

}