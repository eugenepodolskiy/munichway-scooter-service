package com.example.munichway.controllers;

import com.example.munichway.models.Scooter;
import com.example.munichway.services.ScooterService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/{id}/rent")
    public Scooter rentScooter(@PathVariable Long id,
                               @org.springframework.web.bind.annotation.RequestParam Long userId) {
        return scooterService.rentScooter(id, userId);
    }


    @PostMapping("/{id}/return")
    public Scooter returnScooter(@PathVariable Long id,
                                 @jakarta.validation.Valid @RequestBody com.example.munichway.DTO.ReturnRequest request) {

        return scooterService.returnScooter(id, request);
    }

    @GetMapping("/available")
    public List<Scooter> getAvailableScooters() {
        return scooterService.getAvailableScooters();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Scooter addScooter(@Valid @RequestBody com.example.munichway.DTO.ScooterCreateRequest request) {
        return scooterService.addScooter(request);
    }

}
