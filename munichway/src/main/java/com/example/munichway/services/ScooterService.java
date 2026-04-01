package com.example.munichway.services;

import com.example.munichway.exceptions.InsufficientFundsException;
import com.example.munichway.models.Scooter;
import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import com.example.munichway.repositories.ScooterRepository;
import com.example.munichway.repositories.TripRepository;
import com.example.munichway.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScooterService {

    private final ScooterRepository scooterRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Value("${billing.unlock-fee}")
    private double unlockFee;

    @Value("${billing.minute-rate}")
    private double minuteRate;

    @Value("${billing.min-balance-to-rent}")
    private double minBalanceToRent;

    public ScooterService(ScooterRepository scooterRepository, TripRepository tripRepository, UserRepository userRepository) {
        this.scooterRepository = scooterRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public Page<Scooter> findAll(Pageable pageable) {
        return scooterRepository.findAll(pageable);
    }

    @Transactional
    public Scooter rentScooter(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getBalance() < minBalanceToRent) {
            throw new InsufficientFundsException("Minimum " + minBalanceToRent + " required to start rental");
        }

        Scooter scooter = scooterRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scooter not found"));

        if (!scooter.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scooter is already rented");
        }

        user.setBalance(Math.round((user.getBalance() - unlockFee) * 100.0) / 100.0);
        userRepository.save(user);

        scooter.setAvailable(false);

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setScooter(scooter);
        trip.setStartTime(LocalDateTime.now());

        tripRepository.save(trip);
        return scooterRepository.save(scooter);
    }


    @Transactional
    public Scooter returnScooter(Long id, com.example.munichway.DTO.ReturnRequest request, String email) {

        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scooter not found"));

        if (scooter.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scooter is already at the parking");
        }

        Trip activeTrip = tripRepository.findByScooterIdAndEndTimeIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active trip not found"));

        if (!activeTrip.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot return a scooter you didn't rent!");
        }

        activeTrip.setEndTime(LocalDateTime.now());
        long minutes = Duration.between(activeTrip.getStartTime(), activeTrip.getEndTime()).toMinutes();

        double cost = Math.round((unlockFee + (minutes * minuteRate)) * 100.0) / 100.0;
        activeTrip.setTotalCost(cost);

        scooter.setAvailable(true);
        scooter.setLocation(request.getNewLocation());
        scooter.setBatteryLevel(request.getNewBatteryLevel());

        tripRepository.save(activeTrip);
        return scooterRepository.save(scooter);
    }

    public List<Scooter> getAvailableScooters() {
        return scooterRepository.findByAvailableTrue();
    }

    public Scooter addScooter(com.example.munichway.DTO.ScooterCreateRequest request) {
        Scooter scooter = new Scooter();
        scooter.setModelName(request.getModelName());
        scooter.setLocation(request.getLocation());
        scooter.setBatteryLevel(request.getBatteryLevel());
        scooter.setAvailable(true);
        return scooterRepository.save(scooter);
    }
}