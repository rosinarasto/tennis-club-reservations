package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.model.factory.UserFactory;
import com.tennisclub.reservations.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "data.init.enabled=false")
public class ReservationServiceTest {

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private CourtService courtService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Test
    public void createReservation_newUser() {
        var user = UserFactory.createUser("gg", "123456789");
        var court = CourtFactory.createCourt(4, new ArrayList<>());

        when(userService.getOrCreate(user))
                .thenReturn(user);

        when(courtService.findByNumber(4))
                .thenReturn(court);

        var reservation = ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30));
        reservation.setUser(user);
        reservation.setCourt(court);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var actual = reservationService.create(reservation);

        assertThat(actual).isNotNull();
        assertThat(actual.getUser().getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getUser().getName()).isEqualTo("gg");
    }

    @Test
    public void createReservation_existingUser() {
        var user = UserFactory.createUser("gg", "123456789");
        var court = CourtFactory.createCourt(4, new ArrayList<>());

        when(userService.getOrCreate(user))
                .thenReturn(user);

        when(courtService.findByNumber(4))
                .thenReturn(court);

        var reservation = ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30));
        reservation.setUser(user);
        reservation.setCourt(court);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var actual = reservationService.create(reservation);

        assertThat(actual).isNotNull();
        assertThat(actual.getUser().getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getUser().getName()).isEqualTo("gg");
    }

    @Test
    public void isDateAvailable_returnsRepositoryResult() {
        var from = getTime(12, 0);
        var to = getTime(13, 30);

        when(reservationRepository.isDateAvailable(4, from, to))
                .thenReturn(true);

        assertThat(reservationService.isDateAvailable(4, from, to)).isTrue();
        verify(reservationRepository).isDateAvailable(4, from, to);
    }

    @Test
    public void findByCourtNumber_returnsEmptyList() {
        when(reservationRepository.findByCourtNumberOrderByCreationDate(4))
                .thenReturn(List.of());

        assertThat(reservationService.findByCourtNumber(4).isEmpty()).isTrue();
    }

    @Test
    public void findByCourtNumber_returnsReservations() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50))
        );

        when(reservationRepository.findByCourtNumberOrderByCreationDate(4))
                .thenReturn(reservations);

        assertThat(reservationService.findByCourtNumber(4)).isEqualTo(reservations);
    }

    @Test
    public void findByUserPhoneNumber_returnsReservations() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50))
        );

        when(reservationRepository.findByUserPhoneNumberOrderByFrom(
                eq("123456789"),
                eq(true),
                any(LocalDateTime.class)
        )).thenReturn(reservations);

        assertThat(reservationService.findByUserPhoneNumber("123456789", true)).isEqualTo(reservations);
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
