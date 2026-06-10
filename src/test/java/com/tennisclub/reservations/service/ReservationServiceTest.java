package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.entity.User;
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
import java.util.Optional;

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

        when(userService.getOrCreate(any(User.class)))
                .thenReturn(user);

        court.setId(1L);
        when(courtService.findById(1L))
                .thenReturn(Optional.of(court));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var createDto = createReservationCreateDto();
        var actual = reservationService.create(createDto);

        assertThat(actual).isNotNull();
        assertThat(actual.getUser().getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getUser().getName()).isEqualTo("gg");
    }

    @Test
    public void createReservation_existingUser() {
        var user = UserFactory.createUser("gg", "123456789");
        var court = CourtFactory.createCourt(4, new ArrayList<>());

        when(userService.getOrCreate(any(User.class)))
                .thenReturn(user);

        court.setId(1L);
        when(courtService.findById(1L))
                .thenReturn(Optional.of(court));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var createDto = createReservationCreateDto();
        var actual = reservationService.create(createDto);

        assertThat(actual).isNotNull();
        assertThat(actual.getUser().getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getUser().getName()).isEqualTo("gg");
    }

    @Test
    public void isDateAvailable_returnsRepositoryResult() {
        var from = getTime(12, 0);
        var to = getTime(13, 30);

        when(reservationRepository.isDateAvailable(1L, from, to))
                .thenReturn(true);

        assertThat(reservationService.isDateAvailable(1L, from, to)).isTrue();
        verify(reservationRepository).isDateAvailable(1L, from, to);
    }

    @Test
    public void updateReservation_usesManagedCourtAndUser() {
        var user = UserFactory.createUser("gg", "123456789");
        var court = CourtFactory.createCourt(4, new ArrayList<>());
        court.setId(1L);
        var updateDto = ReservationFactory.createUpdateDto(1L);

        when(userService.getOrCreate(any(User.class)))
                .thenReturn(user);

        when(courtService.findById(1L))
                .thenReturn(Optional.of(court));

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30))));

        when(reservationRepository.update(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var actual = reservationService.update(updateDto);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getUser()).isEqualTo(user);
        assertThat(actual.getCourt()).isEqualTo(court);
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

    private ReservationCreateDto createReservationCreateDto() {
        return new ReservationCreateDto(
                getTime(12, 0),
                getTime(13, 30),
                GameType.SINGLES,
                UserFactory.createCreateDto(),
                1L
        );
    }
}
