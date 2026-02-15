package com.example.munichway.repositories;

import com.example.munichway.models.Scooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {

    List<Scooter> findByIsAvailableTrue();
}
