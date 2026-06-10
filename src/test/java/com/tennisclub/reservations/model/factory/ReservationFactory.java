package com.tennisclub.reservations.model.factory;

import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.model.dto.update.ReservationUpdateDto;
import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.model.entity.Reservation;

import java.time.LocalDateTime;

public class ReservationFactory {

    private static final LocalDateTime DEFAULT_FROM = LocalDateTime.of(2026, 12, 31, 13, 30);
    private static final LocalDateTime DEFAULT_TO = LocalDateTime.of(2026, 12, 31, 15, 0);

    private static final GameType DEFAULT_GAME_TYPE = GameType.SINGLES;

    public static Reservation createReservation(LocalDateTime from, LocalDateTime to) {
        return new Reservation(from, to, DEFAULT_GAME_TYPE, UserFactory.createUser(), CourtFactory.createCourt(4));
    }

    public static ReservationDto createDto() {
        return new ReservationDto(DEFAULT_FROM, DEFAULT_TO, DEFAULT_GAME_TYPE, UserFactory.createDto(), CourtFactory.createDto(4));
    }

    public static ReservationCreateDto createCreateDto(Long courtId, LocalDateTime from, LocalDateTime to) {
        return new ReservationCreateDto(from, to, DEFAULT_GAME_TYPE, UserFactory.createCreateDto(), courtId);
    }

    public static ReservationCreateDto createCreateDto() {
        return new ReservationCreateDto(DEFAULT_FROM, DEFAULT_TO, DEFAULT_GAME_TYPE, UserFactory.createCreateDto(), 1L);
    }

    public static ReservationUpdateDto createUpdateDto(Long id) {
        var dto = new ReservationUpdateDto(DEFAULT_FROM, DEFAULT_TO, DEFAULT_GAME_TYPE, UserFactory.createCreateDto(), 1L);
        dto.setId(id);
        return dto;
    }
}
