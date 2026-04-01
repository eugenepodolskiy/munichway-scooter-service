package com.example.munichway.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "scooters")
@SQLDelete(sql = "UPDATE scooters SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
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
    @Column(nullable = false)
    private Integer batteryLevel;

    @Column(name = "available", nullable = false)
    private boolean available;

    @NotBlank(message = "Location is required")
    private String location;

    @Column(nullable = false)
    private boolean deleted = false;

    public Scooter() {
    }

}