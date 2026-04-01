package com.example.munichway.controllers;

import com.example.munichway.models.Scooter;
import com.example.munichway.services.ScooterService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scooters")
public class ScooterController {

    private final ScooterService scooterService;

    public ScooterController(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @GetMapping
    public Page<Scooter> getAllScooters(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return scooterService.findAll(pageable);
    }

    @PostMapping("/{scooterId}/rent")
    public Scooter rentScooter(@PathVariable Long scooterId,
                               @AuthenticationPrincipal UserDetails userDetails) {
        return scooterService.rentScooter(scooterId, userDetails.getUsername());
    }

    @PostMapping("/{scooterId}/return")
    public Scooter returnScooter(@PathVariable Long scooterId,
                                 @Valid @RequestBody com.example.munichway.DTO.ReturnRequest request,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        return scooterService.returnScooter(scooterId, request, userDetails.getUsername());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Scooter>> getAvailableScootersNear(
            @RequestParam double lat,
            @RequestParam double lon) {
        return ResponseEntity.ok(scooterService.getAvailableScootersNear(lat, lon));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Scooter addScooter(@Valid @RequestBody com.example.munichway.DTO.ScooterCreateRequest request) {
        return scooterService.addScooter(request);
    }
}