package com.example.munichway.controllers;

import com.example.munichway.DTO.LoginRequest;
import com.example.munichway.DTO.TokenResponse;
import com.example.munichway.DTO.TripResponse;
import com.example.munichway.DTO.UserCreateRequest;
import com.example.munichway.models.User;
import com.example.munichway.services.AuthenticationService;
import com.example.munichway.services.TripService;
import com.example.munichway.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final TripService tripService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, TripService tripService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.tripService = tripService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/me/trips")
    public List<TripResponse> getMyTrips(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return tripService.getUserTrips(user.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/me/top-up")
    public User topUpBalance(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody com.example.munichway.DTO.TopUpRequest request) {
        return userService.topUpBalanceByEmail(userDetails.getUsername(), request.getAmount());
    }
}
