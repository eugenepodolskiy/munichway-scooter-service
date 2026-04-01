package com.example.munichway.services;

import com.example.munichway.models.Scooter;
import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import com.example.munichway.repositories.ScooterRepository;
import com.example.munichway.repositories.TripRepository;
import com.example.munichway.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;

    @Value("${billing.minute-rate}")
    private double minuteRate;

    @Value("${billing.max-debt-limit}")
    private double maxDebtLimit;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void processActiveTrips() {
        log.info("Starting billing process: checking active trips...");
        List<Trip> activeTrips = tripRepository.findAllByEndTimeIsNull();

        for (Trip trip : activeTrips) {

            User user = trip.getUser();
            Scooter scooter = trip.getScooter();

            user.setBalance(user.getBalance()-minuteRate);

            log.info("Deducted {} from user {} for trip {}. Remaining balance: {}",
                    minuteRate, user.getEmail(), trip.getId(), user.getBalance());

            if (user.getBalance() <= maxDebtLimit) {
                log.warn("User {} exceeded debt limit ({}). Forcibly stopping trip {}...",
                        user.getEmail(), maxDebtLimit, trip.getId());
                trip.setEndTime(LocalDateTime.now());
                scooter.setAvailable(true);
            }

        userRepository.save(user);
        tripRepository.save(trip);
        scooterRepository.save(scooter);

        }

        log.info("Billing process completed. Processed {} active trips.", activeTrips.size());
        }

}