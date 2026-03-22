package com.example.munichway.services;

import com.example.munichway.DTO.TripResponse;
import com.example.munichway.models.User;
import com.example.munichway.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TripService {

    private final UserRepository userRepository;

    public TripService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<TripResponse> getUserTrips(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        return user.getTrips().stream().map(trip -> {
            TripResponse dto = new TripResponse();
            dto.setTripId(trip.getId());
            dto.setScooterModel(trip.getScooter().getModelName());
            dto.setStartTime(trip.getStartTime());
            dto.setEndTime(trip.getEndTime());
            dto.setTotalCost(trip.getTotalCost());
            return dto;
        }).toList();
    }
}