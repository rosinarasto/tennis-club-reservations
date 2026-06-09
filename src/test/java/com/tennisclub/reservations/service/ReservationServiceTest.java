package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.entity.User;
import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.model.factory.UserFactory;
import com.tennisclub.reservations.repository.CourtRepository;
import com.tennisclub.reservations.repository.ReservationRepository;
import com.tennisclub.reservations.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ReservationServiceTest {

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private CourtRepository courtRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ReservationService reservationService;

    @Test
    public void createReservation_newUser() {
        var user = UserFactory.createUser("gg", "123456789");
        var userDTO = UserFactory.createCreateDto("gg", "123456789");
        var court = CourtFactory.createCourt(4, new ArrayList<>());

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.empty(), Optional.empty(), Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        var reservationDTOs = ReservationFactory.createCreateDto(getTime(12, 0), getTime(13, 30), userDTO);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var actual = reservationService.create(reservationDTOs);

        assertThat(actual).isNotNull();
        assertThat(actual.getUser().getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getUser().getName()).isEqualTo("gg");
    }

    @Test
    public void createReservation_existingUser() {
        var user = UserFactory.createUser("gg", "123456789");
        var userDTO = UserFactory.createCreateDto("gg", "123456789");
        var court = CourtFactory.createCourt(4, new ArrayList<>());

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.of(user));

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        var reservationDTOs = ReservationFactory.createCreateDto(getTime(12, 0), getTime(13, 30), userDTO);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var actual = reservationService.create(reservationDTOs);

        assertThat(actual).isNotNull();
        assertThat(actual.getUser().getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getUser().getName()).isEqualTo("gg");
    }

    @Test
    public void isDateAvailable_false1() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30))
        );

        var court = CourtFactory.createCourt(4, reservations);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThat(reservationService.isDateAvailable(4, getTime(11, 30), getTime(12, 15))).isFalse();
    }

    @Test
    public void isDateAvailable_false2() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30))
        );

        var court = CourtFactory.createCourt(4, reservations);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThat(reservationService.isDateAvailable(4, getTime(12, 30), getTime(13, 15))).isFalse();
    }

    @Test
    public void isDateAvailable_falseWhenExactSameInterval() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30))
        );

        var court = CourtFactory.createCourt(4, reservations);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThat(reservationService.isDateAvailable(4, getTime(12, 0), getTime(13, 30))).isFalse();
    }

    @Test
    public void isDateAvailable_falseWhenNewIntervalContainsExistingReservation() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30))
        );

        var court = CourtFactory.createCourt(4, reservations);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThat(reservationService.isDateAvailable(4, getTime(11, 30), getTime(14, 0))).isFalse();
    }

    @Test
    public void isDateAvailable_true() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30))
        );

        var court = CourtFactory.createCourt(4, reservations);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThat(reservationService.isDateAvailable(4, getTime(13, 31), getTime(14, 15))).isTrue();
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
