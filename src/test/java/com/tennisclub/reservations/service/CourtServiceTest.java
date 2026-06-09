package com.tennisclub.reservations.service;

import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.Court;
import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.repository.CourtRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CourtServiceTest {

    @MockitoBean
    private CourtRepository courtRepository;

    @Autowired
    private CourtService courtService;

    @Test
    public void createCourt_throwsException() {
        var court = CourtFactory.createCourt(4);
        var createDTO = CourtFactory.createCreateDto(4);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThatExceptionOfType(ResourceAlreadyExistsException.class)
                .isThrownBy(() -> courtService.create(createDTO))
                .withMessage("Court with number already exists");
    }

    @Test
    public void createCourt() {
        var court = CourtFactory.createCourt(4);
        var createDTO = CourtFactory.createCreateDto(4);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.empty());

        when(courtRepository.save(any(Court.class)))
            .thenReturn(court);

        assertThat(courtService.create(createDTO).getNumber()).isEqualTo(4);
    }

    @Test
    public void findReservations_returnsEmptyList() {
        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.empty());

        var actual = courtService.findReservations(4);

        assertThat(actual).isEmpty();
    }

    @Test
    public void findReservations_returnsReservations() {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50))
        );

        var court = CourtFactory.createCourt(4, reservations);

        var reservationDTOs = List.of(
                ReservationFactory.createDto(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createDto(getTime(14, 0), getTime(15, 50))
        );

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        var actual = courtService.findReservations(4);

        assertThat(actual).isEqualTo(reservationDTOs);
    }

    @Test
    public void findReservations_returnsReservationsOrderedByCreationDate() {
        var laterCreatedReservation = ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50));
        laterCreatedReservation.setCreationDate(LocalDate.of(2024, 1, 2).atStartOfDay());

        var earlierCreatedReservation = ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30));
        earlierCreatedReservation.setCreationDate(LocalDate.of(2024, 1, 1).atStartOfDay());

        var court = CourtFactory.createCourt(4, List.of(laterCreatedReservation, earlierCreatedReservation));

        var reservationDTOs = List.of(
                ReservationFactory.createDto(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createDto(getTime(14, 0), getTime(15, 50))
        );

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        var actual = courtService.findReservations(4);

        assertThat(actual).isEqualTo(reservationDTOs);
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
