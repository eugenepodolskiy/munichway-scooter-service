package com.example.munichway.services;

import com.example.munichway.models.Scooter;
import com.example.munichway.repositories.ScooterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScooterServiceTest {

    @Mock
    private ScooterRepository scooterRepository;

    @InjectMocks
    private ScooterService scooterService;

    @Test
    void shouldRentScooterSuccessfully() {


        Scooter testScooter = new Scooter();
        testScooter.setId(1L);
        testScooter.setAvailable(true);


        when(scooterRepository.findById(1L)).thenReturn(Optional.of(testScooter));
        when(scooterRepository.save(any(Scooter.class))).thenReturn(testScooter);

        Scooter rentedScooter = scooterService.rentScooter(1L);

        assertFalse(rentedScooter.getAvailable(), "Scooter is rented now");


        verify(scooterRepository, times(1)).save(testScooter);
    }

    @Test
    void shouldThrowExceptionWhenScooterIsOccupied() {

        Scooter occupiedScooter = new Scooter();
        occupiedScooter.setId(2L);
        occupiedScooter.setAvailable(false);


        when(scooterRepository.findById(2L)).thenReturn(Optional.of(occupiedScooter));


        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            scooterService.rentScooter(2L);
        });


        verify(scooterRepository, never()).save(any(Scooter.class));
    }

    @Test
    void shouldReturnScooterSuccessfully() {

        Scooter rentedScooter = new Scooter();
        rentedScooter.setId(3L);
        rentedScooter.setAvailable(false);


        when(scooterRepository.findById(3L)).thenReturn(Optional.of(rentedScooter));
        when(scooterRepository.save(any(Scooter.class))).thenReturn(rentedScooter);

        scooterService.returnScooter(3L);

        assertTrue(rentedScooter.getAvailable(), "Scooter should be available");
        verify(scooterRepository, times(1)).save(rentedScooter);
    }
}