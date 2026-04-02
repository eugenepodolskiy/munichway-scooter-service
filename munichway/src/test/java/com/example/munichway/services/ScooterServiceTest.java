package com.example.munichway.services;

import com.example.munichway.DTO.ReturnRequest;
import com.example.munichway.DTO.ScooterCreateRequest;
import com.example.munichway.exceptions.InsufficientFundsException;
import com.example.munichway.models.Scooter;
import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import com.example.munichway.repositories.ScooterRepository;
import com.example.munichway.repositories.TripRepository;
import com.example.munichway.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
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

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ScooterService scooterService;

    private Scooter defaultScooter;
    private User defaultUser;
    private Trip defaultTrip;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(scooterService, "unlockFee", 1.0);
        ReflectionTestUtils.setField(scooterService, "minuteRate", 0.2);
        ReflectionTestUtils.setField(scooterService, "minBalanceToRent", 5.0);

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
    @DisplayName("Should return a paginated list of scooters")
    void shouldFindAllScooters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Scooter> scooterPage = new PageImpl<>(List.of(defaultScooter));

        given(scooterRepository.findAll(pageable)).willReturn(scooterPage);

        Page<Scooter> result = scooterService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(scooterRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should successfully rent a scooter")
    void shouldRentScooterSuccessfully() {
        given(userRepository.findByEmail("user@munichway.com")).willReturn(Optional.of(defaultUser));
        given(scooterRepository.findByIdWithLock(1L)).willReturn(Optional.of(defaultScooter));
        given(scooterRepository.save(any(Scooter.class))).willReturn(defaultScooter);

        Scooter rentedScooter = scooterService.rentScooter(1L, "user@munichway.com");

        assertThat(rentedScooter.isAvailable()).isFalse();
        assertThat(defaultUser.getBalance()).isEqualTo(49.0);
        verify(tripRepository).save(any(Trip.class));
        verify(scooterRepository).save(defaultScooter);
    }

    @Test
    @DisplayName("Should throw exception when renting with insufficient funds")
    void shouldThrowExceptionWhenRentingWithLowBalance() {
        defaultUser.setBalance(3.0);
        given(userRepository.findByEmail("user@munichway.com")).willReturn(Optional.of(defaultUser));

        assertThatThrownBy(() -> scooterService.rentScooter(1L, "user@munichway.com"))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Minimum 5.0 required");

        verify(scooterRepository, never()).findByIdWithLock(any());
    }

    @Test
    @DisplayName("Should throw exception when renting an already rented scooter")
    void shouldThrowExceptionWhenScooterAlreadyRented() {
        defaultScooter.setAvailable(false);
        given(userRepository.findByEmail("user@munichway.com")).willReturn(Optional.of(defaultUser));
        given(scooterRepository.findByIdWithLock(1L)).willReturn(Optional.of(defaultScooter));

        assertThatThrownBy(() -> scooterService.rentScooter(1L, "user@munichway.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already rented");
    }

    @Test
    @DisplayName("Should return scooter and update data")
    void shouldReturnScooterAndUpdateData() {
        defaultScooter.setAvailable(false);
        ReturnRequest request = new ReturnRequest();
        request.setNewLatitude(48.1390);
        request.setNewLongitude(11.5800);
        request.setNewBatteryLevel(85);

        given(scooterRepository.findById(1L)).willReturn(Optional.of(defaultScooter));
        given(tripRepository.findByScooterIdAndEndTimeIsNull(1L)).willReturn(Optional.of(defaultTrip));
        given(scooterRepository.save(any(Scooter.class))).willReturn(defaultScooter);

        Scooter returnedScooter = scooterService.returnScooter(1L, request, "user@munichway.com");

        assertThat(returnedScooter.isAvailable()).isTrue();
        assertThat(returnedScooter.getBatteryLevel()).isEqualTo(85);
        assertThat(defaultTrip.getEndTime()).isNotNull();
        verify(scooterRepository).save(defaultScooter);
    }

    @Test
    @DisplayName("Should throw exception when returning wrong user's scooter")
    void shouldThrowExceptionWhenReturningWrongUserScooter() {
        defaultScooter.setAvailable(false);
        ReturnRequest request = new ReturnRequest();

        User wrongUser = new User();
        wrongUser.setEmail("hacker@munichway.com");
        defaultTrip.setUser(wrongUser);

        given(scooterRepository.findById(1L)).willReturn(Optional.of(defaultScooter));
        given(tripRepository.findByScooterIdAndEndTimeIsNull(1L)).willReturn(Optional.of(defaultTrip));

        assertThatThrownBy(() -> scooterService.returnScooter(1L, request, "user@munichway.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("cannot return a scooter you didn't rent");
    }

    @Test
    @DisplayName("Should throw exception when returning non-existent scooter")
    void shouldThrowExceptionWhenReturningNonExistentScooter() {
        given(scooterRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> scooterService.returnScooter(99L, new ReturnRequest(), "user@munichway.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Scooter not found");

        verify(tripRepository, never()).findByScooterIdAndEndTimeIsNull(any());
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
}