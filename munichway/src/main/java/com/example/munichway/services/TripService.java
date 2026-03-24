package com.example.munichway.services;

import com.example.munichway.DTO.TripResponse;
import com.example.munichway.mappers.TripMapper;
import com.example.munichway.models.User;
import com.example.munichway.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TripService {

    private final UserRepository userRepository;
    private final TripMapper tripMapper;

    public TripService(UserRepository userRepository, TripMapper tripMapper) {
        this.userRepository = userRepository;
        this.tripMapper = tripMapper;
    }

    public List<TripResponse> getUserTrips(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getTrips().stream()
                .map(tripMapper::toDto)
                .toList();
    }
}