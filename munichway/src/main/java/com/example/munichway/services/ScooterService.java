package com.example.munichway.services;


import com.example.munichway.models.Scooter;
import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import com.example.munichway.repositories.ScooterRepository;
import com.example.munichway.repositories.TripRepository;
import com.example.munichway.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScooterService {

    private final ScooterRepository scooterRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;


    public ScooterService(ScooterRepository scooterRepository, TripRepository tripRepository, UserRepository userRepository) {
        this.scooterRepository = scooterRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public List<Scooter> findAll() {

        return scooterRepository.findAll();
    }

    public Scooter save(Scooter scooter) {


        if (scooter.getBatteryLevel() < 0 || scooter.getBatteryLevel() > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }

        return scooterRepository.save(scooter);
    }

    public Scooter rentScooter(Long id, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No scooter with this id"));


        if (!scooter.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It's already rented");
        }


        scooter.setAvailable(false);

        Trip trip = new Trip();

        trip.setUser(user);

        trip.setStartTime(LocalDateTime.now());

        trip.setScooter(scooter);

        tripRepository.save(trip);


        return scooterRepository.save(scooter);
    }

    public Scooter returnScooter(Long id, com.example.munichway.dto.ReturnRequest request) {

        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No scooter with this id"));


        if (scooter.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It is available");
        }


        scooter.setAvailable(true);

        scooter.setLocation(request.getNewLocation());

        scooter.setBatteryLevel(request.getNewBatteryLevel());

        Trip activeTrip = tripRepository.findByScooterIdAndEndTimeIsNull(id)
                .orElseThrow(() -> new RuntimeException("Active trip not found for this scooter"));

        activeTrip.setEndTime(LocalDateTime.now());

        long minutes = java.time.Duration.between(activeTrip.getStartTime(), activeTrip.getEndTime()).toMinutes();

        double cost = 1.0 + minutes*0.1;

        activeTrip.setTotalCost(cost);

        User user = activeTrip.getUser();
        if (user == null) {
            throw new RuntimeException("This trip has no user attached!");
        }

        user.setBalance(user.getBalance() - cost);
        userRepository.save(user);

        tripRepository.save(activeTrip);

        return scooterRepository.save(scooter);
    }

    public List<Scooter> getAvailableScooters() {
        return scooterRepository.findByIsAvailableTrue();
    }


}
