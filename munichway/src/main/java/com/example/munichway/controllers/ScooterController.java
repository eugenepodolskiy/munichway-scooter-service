package com.example.munichway.controllers;

import com.example.munichway.models.Scooter;
import com.example.munichway.services.ScooterService;
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
    public List<Scooter> getAllScooters() {
        return scooterService.findAll();
    }


    @PostMapping
    public Scooter createScooter(@jakarta.validation.Valid @RequestBody Scooter scooter) {
        return scooterService.save(scooter);
    }

    @PostMapping("/{id}/rent")
    public Scooter rentScooter(@PathVariable Long id,
                               @org.springframework.web.bind.annotation.RequestParam Long userId) {
        return scooterService.rentScooter(id, userId);
    }


    @PostMapping("/{id}/return")
    public Scooter returnScooter(@PathVariable Long id,
                                 @jakarta.validation.Valid @RequestBody com.example.munichway.dto.ReturnRequest request) {

        return scooterService.returnScooter(id, request);
    }

    @GetMapping("/available")
    public List<Scooter> getAvailableScooters() {
        return scooterService.getAvailableScooters();
    }

}
