package com.example.munichway.repositories;

import com.example.munichway.models.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository  extends JpaRepository<Trip, Long> {
    Optional<Trip> findByScooterIdAndEndTimeIsNull(Long scooterId);
    List<Trip> findAllByEndTimeIsNull();
}
