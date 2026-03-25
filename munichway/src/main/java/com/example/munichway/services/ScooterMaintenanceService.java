package com.example.munichway.services;

import com.example.munichway.models.Scooter;
import com.example.munichway.repositories.ScooterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScooterMaintenanceService {

    private final ScooterRepository scooterRepository;


    @Scheduled(fixedRate = 60000)
    public void rechargeAvailableScooters() {
        log.info("System check: looking for scooters with battery < 50%...");

        List<Scooter> scootersToCharge = scooterRepository.findByIsAvailableTrueAndBatteryLevelLessThan(50);

        if (scootersToCharge.isEmpty()) {
            log.info("All available scooters are charged. Going back to sleep.");
            return;
        }

        log.info("Starting maintenance: charging {} scooters...", scootersToCharge.size());

        for (Scooter scooter : scootersToCharge) {
            int newBattery = Math.min(scooter.getBatteryLevel() + 20, 100);
            scooter.setBatteryLevel(newBattery);
            log.info("Maintenance complete. Scooters updated.");
        }

        scooterRepository.saveAll(scootersToCharge);
    }
}