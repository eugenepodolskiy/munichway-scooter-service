package com.example.munichway.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double totalCost;

    @ManyToOne
    @JoinColumn(name = "scooter_id", nullable = false)
    private Scooter scooter;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Trip() {
    }

}
