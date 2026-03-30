package com.example.munichway.repositories;

import com.example.munichway.models.Scooter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {

    List<Scooter> findByAvailableTrue();
    List<Scooter> findByAvailableTrueAndBatteryLevelLessThan(Integer batteryLevel);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Scooter s WHERE s.id = :id")
    java.util.Optional<Scooter> findByIdWithLock(@Param("id") Long id);
}
