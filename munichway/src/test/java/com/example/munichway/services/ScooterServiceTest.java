package com.example.munichway.services;

import com.example.munichway.exceptions.InsufficientFundsException;
import com.example.munichway.models.Scooter;
import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import com.example.munichway.repositories.ScooterRepository;
import com.example.munichway.repositories.TripRepository;
import com.example.munichway.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private User testUser;
    private Scooter testScooter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scooterService, "minBalanceToRent", 5.0);
        ReflectionTestUtils.setField(scooterService, "unlockFee", 1.0);
        ReflectionTestUtils.setField(scooterService, "minuteRate", 0.1);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@munichway.com");
        testUser.setBalance(10.0);

        testScooter = new Scooter();
        testScooter.setId(1L);
        testScooter.setAvailable(true);
    }

    @Test
    void rentScooter_Success() {
        when(userRepository.findByEmail("test@munichway.com")).thenReturn(Optional.of(testUser));
        when(scooterRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testScooter));
        when(scooterRepository.save(any(Scooter.class))).thenReturn(testScooter);

        Scooter result = scooterService.rentScooter(1L, "test@munichway.com");

        assertFalse(result.isAvailable());
        assertEquals(9.0, testUser.getBalance());
        verify(tripRepository, times(1)).save(any());
    }

    @Test
    void rentScooter_ThrowsException_WhenInsufficientFunds() {

        testUser.setBalance(3.0);
        when(userRepository.findByEmail("test@munichway.com")).thenReturn(Optional.of(testUser));

        assertThrows(InsufficientFundsException.class, () ->
                scooterService.rentScooter(1L, "test@munichway.com")
        );

        verify(scooterRepository, never()).findByIdWithLock(anyLong());
        verify(tripRepository, never()).save(any());
    }


    @Test
    void rentScooter_ThrowsException_WhenScooterAlreadyRented() {
        testScooter.setAvailable(false);
        when(userRepository.findByEmail("test@munichway.com")).thenReturn(Optional.of(testUser));
        when(scooterRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testScooter));
        assertThrows(ResponseStatusException.class, () ->
                scooterService.rentScooter(1L, "test@munichway.com")
        );
        verify(tripRepository, never()).save(any());
    }

    @Test
    void returnScooter_Success(){
        testScooter.setAvailable(false);
        Trip activeTrip = new Trip();
        activeTrip.setId(1L);
        activeTrip.setUser(testUser);
        activeTrip.setScooter(testScooter);
        activeTrip.setStartTime(LocalDateTime.now().minusMinutes(10));

        com.example.munichway.DTO.ReturnRequest request = new com.example.munichway.DTO.ReturnRequest();
        request.setNewLocation("Odeonsplatz");
        request.setNewBatteryLevel(80);

        when(scooterRepository.findById(1L)).thenReturn(Optional.of(testScooter));
        when(tripRepository.findByScooterIdAndEndTimeIsNull(1L)).thenReturn(Optional.of(activeTrip));
        when(scooterRepository.save(any(Scooter.class))).thenReturn(testScooter);

        Scooter result = scooterService.returnScooter(1L, request, "test@munichway.com");

        assertTrue(result.isAvailable());
        assertEquals("Odeonsplatz", result.getLocation());
        assertEquals(80, result.getBatteryLevel());

        assertNotNull(activeTrip.getEndTime());
        assertEquals(2.0, activeTrip.getTotalCost());

        verify(tripRepository, times(1)).save(activeTrip);
        verify(userRepository, never()).save(any());
    }

}