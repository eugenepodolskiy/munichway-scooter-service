package com.example.munichway.services;

import com.example.munichway.DTO.ReturnRequest;
import com.example.munichway.DTO.ScooterCreateRequest;
import com.example.munichway.models.Scooter;
import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import com.example.munichway.repositories.ScooterRepository;
import com.example.munichway.repositories.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScooterServiceTest {

    @Mock
    private ScooterRepository scooterRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private ScooterService scooterService;

    private Scooter defaultScooter;
    private User defaultUser;
    private Trip defaultTrip;

    @BeforeEach
    void setUp() {
        defaultScooter = new Scooter();
        defaultScooter.setId(1L);
        defaultScooter.setModelName("Ninebot Max G30");
        defaultScooter.setAvailable(true);
        defaultScooter.setBatteryLevel(100);

        defaultUser = new User();
        defaultUser.setEmail("user@munichway.com");
        defaultUser.setBalance(50.0);

        defaultTrip = new Trip();
        defaultTrip.setId(10L);
        defaultTrip.setScooter(defaultScooter);
        defaultTrip.setUser(defaultUser);

        defaultTrip.setStartTime(LocalDateTime.now().minusMinutes(15));
    }

    @Test
    @DisplayName("Should add scooter successfully")
    void shouldAddScooterSuccessfully() {
        ScooterCreateRequest request = new ScooterCreateRequest();
        request.setModelName("Ninebot Max G30");
        request.setBatteryLevel(100);
        request.setLatitude(48.1371);
        request.setLongitude(11.5756);

        given(scooterRepository.save(any(Scooter.class))).willReturn(defaultScooter);

        Scooter savedScooter = scooterService.addScooter(request);

        assertThat(savedScooter).isNotNull();
        assertThat(savedScooter.getModelName()).isEqualTo("Ninebot Max G30");
        verify(scooterRepository).save(any(Scooter.class));
    }

    @Test
    @DisplayName("Should return available scooters near location")
    void shouldReturnAvailableScootersNearLocation() {
        double lat = 48.1371;
        double lon = 11.5756;
        given(scooterRepository.findAvailableNearLocation(lat, lon)).willReturn(List.of(defaultScooter));

        List<Scooter> scooters = scooterService.getAvailableScootersNear(lat, lon);

        assertThat(scooters).hasSize(1);
        assertThat(scooters.get(0).getId()).isEqualTo(1L);
        verify(scooterRepository).findAvailableNearLocation(lat, lon);
    }

    @Test
    @DisplayName("Should return scooter and update data")
    void shouldReturnScooterAndUpdateData() {
        Long scooterId = 1L;
        String email = "user@munichway.com";
        defaultScooter.setAvailable(false);

        ReturnRequest request = new ReturnRequest();
        request.setNewLatitude(48.1390);
        request.setNewLongitude(11.5800);
        request.setNewBatteryLevel(85);

        given(scooterRepository.findById(scooterId)).willReturn(Optional.of(defaultScooter));
        given(tripRepository.findByScooterIdAndEndTimeIsNull(scooterId)).willReturn(Optional.of(defaultTrip));
        given(scooterRepository.save(any(Scooter.class))).willReturn(defaultScooter);

        Scooter returnedScooter = scooterService.returnScooter(scooterId, request, email);

        assertThat(returnedScooter.isAvailable()).isTrue();
        verify(scooterRepository).save(defaultScooter);
    }

    @Test
    @DisplayName("Should throw exception when returning non-existent scooter")
    void shouldThrowExceptionWhenReturningNonExistentScooter() {
        Long invalidId = 99L;
        ReturnRequest request = new ReturnRequest();
        String email = "user@munichway.com";

        given(scooterRepository.findById(invalidId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> scooterService.returnScooter(invalidId, request, email))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Scooter not found");

        verify(tripRepository, never()).findByScooterIdAndEndTimeIsNull(any());
        verify(scooterRepository, never()).save(any());
    }
}