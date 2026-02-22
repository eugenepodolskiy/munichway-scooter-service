package com.example.munichway.services;

import com.example.munichway.models.Scooter;
import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import com.example.munichway.repositories.ScooterRepository;
import com.example.munichway.repositories.TripRepository;
import com.example.munichway.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScooterServiceTest {

    @Mock
    private ScooterRepository scooterRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ScooterService scooterService;

    @Test
    void shouldRentScooterSuccessfully() {


        Scooter testScooter = new Scooter();
        testScooter.setId(1L);
        testScooter.setAvailable(true);

        User testUser = new User();
        testUser.setId(1L);


        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(testScooter));
        when(scooterRepository.save(any(Scooter.class))).thenReturn(testScooter);

        Scooter rentedScooter = scooterService.rentScooter(1L, 1L);

        assertFalse(rentedScooter.getAvailable(), "Scooter is rented now");
        verify(tripRepository, times(1)).save(any(Trip.class));
        verify(scooterRepository, times(1)).save(testScooter);
    }

    @Test
    void shouldThrowExceptionWhenScooterIsOccupied() {

        Scooter occupiedScooter = new Scooter();
        occupiedScooter.setId(2L);
        occupiedScooter.setAvailable(false);

        User testUser = new User();
        testUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(scooterRepository.findById(2L)).thenReturn(Optional.of(occupiedScooter));


        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            scooterService.rentScooter(2L, 1L);
        });


        verify(scooterRepository, never()).save(any(Scooter.class));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldReturnScooterSuccessfully() {

        Scooter rentedScooter = new Scooter();
        rentedScooter.setId(3L);
        rentedScooter.setAvailable(false);
        rentedScooter.setLocation("Marienplatz");
        rentedScooter.setBatteryLevel(80);

        User testUser = new User();
        testUser.setId(1L);
        testUser.setBalance(100.0);

        Trip activeTrip = new Trip();
        activeTrip.setScooter(rentedScooter);
        activeTrip.setUser(testUser);
        activeTrip.setStartTime(LocalDateTime.now().minusMinutes(10));

        com.example.munichway.dto.ReturnRequest request = new com.example.munichway.dto.ReturnRequest();
        request.setNewLocation("Olympiapark");
        request.setNewBatteryLevel(45);



        when(scooterRepository.findById(3L)).thenReturn(Optional.of(rentedScooter));
        when(tripRepository.findByScooterIdAndEndTimeIsNull(3L)).thenReturn(Optional.of(activeTrip));
        when(scooterRepository.save(any(Scooter.class))).thenReturn(rentedScooter);

        scooterService.returnScooter(3L, request);

        assertTrue(rentedScooter.getAvailable(), "Scooter should be available again");
        org.junit.jupiter.api.Assertions.assertEquals("Olympiapark", rentedScooter.getLocation(), "Location must be updated");
        org.junit.jupiter.api.Assertions.assertEquals(45, rentedScooter.getBatteryLevel(), "Battery level must be updated");

        assertEquals(98.0, testUser.getBalance(), "Balance should be correctly deducted");

        verify(tripRepository, times(1)).save(activeTrip);
        verify(userRepository, times(1)).save(testUser);
        verify(scooterRepository, times(1)).save(rentedScooter);
    }
}