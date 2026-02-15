package com.example.munichway.services;


import com.example.munichway.models.Scooter;
import com.example.munichway.repositories.ScooterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScooterService {

    private final ScooterRepository scooterRepository;


    public ScooterService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
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

    public Scooter rentScooter(Long id) {

        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No scooter with this id"));


        if (!scooter.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It's already rented");
        }


        scooter.setAvailable(false);


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

        return scooterRepository.save(scooter);
    }

    public List<Scooter> getAvailableScooters() {
        return scooterRepository.findByIsAvailableTrue();
    }

    private void ResponseStatusException(HttpStatus httpStatus, String itIsAvailable) {
    }


}
