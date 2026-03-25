package com.example.munichway.controllers;

import com.example.munichway.DTO.TripResponse;
import com.example.munichway.DTO.UserCreateRequest;
import com.example.munichway.models.User;
import com.example.munichway.services.TripService;
import com.example.munichway.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final TripService tripService;

    public UserController(UserService userService, TripService tripService) {
        this.userService = userService;
        this.tripService = tripService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.registerUser(request);
    }

    @GetMapping("/{id}/trips")
    public List<TripResponse> getUserTrips(@PathVariable Long id) {
        return tripService.getUserTrips(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/top-up")
    public User topUpBalance(
            @PathVariable Long id,
            @Valid @RequestBody com.example.munichway.DTO.TopUpRequest request) {
        return userService.topUpBalance(id, request.getAmount());
    }
}
