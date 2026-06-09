package com.tennisclub.reservations.model.factory;

import com.tennisclub.reservations.model.dto.CourtDto;
import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.SurfaceDto;
import com.tennisclub.reservations.model.dto.UserDto;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.model.dto.create.UserCreateDto;
import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.entity.User;

import java.time.LocalDateTime;

public class ReservationFactory {

    private static final LocalDateTime DEFAULT_FROM = LocalDateTime.of(2026, 12, 31, 13, 30);
    private static final LocalDateTime DEFAULT_TO = LocalDateTime.of(2026, 12, 31, 15, 0);

    private static final User DEFAULT_USER = UserFactory.createUser();
    private static final Court DEFAULT_COURT = CourtFactory.createCourt(4);

    private static final UserDto DEFAULT_USER_DTO = UserFactory.createDto();
    private static final CourtDto DEFAULT_COURT_DTO = CourtFactory.createDto(4);

    private static final UserCreateDto DEFAULT_USER_CREATE_DTO = UserFactory.createCreateDto();

    private static final GameType DEFAULT_GAME_TYPE = GameType.SINGLES;

    public static Reservation createReservation(LocalDateTime from, LocalDateTime to) {
        return new Reservation(from, to, DEFAULT_GAME_TYPE, DEFAULT_USER, DEFAULT_COURT);
    }

    public static ReservationDto createDto(LocalDateTime from, LocalDateTime to, GameType gameType, SurfaceDto surfaceDto) {
        var court = new CourtDto(DEFAULT_COURT_DTO.getName(), DEFAULT_COURT_DTO.getNumber(), surfaceDto);
        return new ReservationDto(from, to, gameType, DEFAULT_USER_DTO, court);
    }

    public static ReservationDto createDto(LocalDateTime from, LocalDateTime to) {
        return new ReservationDto(from, to, DEFAULT_GAME_TYPE, DEFAULT_USER_DTO, DEFAULT_COURT_DTO);
    }

    public static ReservationDto createDto() {
        return new ReservationDto(DEFAULT_FROM, DEFAULT_TO, DEFAULT_GAME_TYPE, DEFAULT_USER_DTO, DEFAULT_COURT_DTO);
    }

    public static ReservationCreateDto createCreateDto(CourtDto courtDto, LocalDateTime from, LocalDateTime to) {
        return new ReservationCreateDto(from, to, DEFAULT_GAME_TYPE, DEFAULT_USER_CREATE_DTO, courtDto);
    }

    public static ReservationCreateDto createCreateDto(LocalDateTime from, LocalDateTime to, UserCreateDto userCreateDto) {
        return new ReservationCreateDto(from, to, DEFAULT_GAME_TYPE, userCreateDto, DEFAULT_COURT_DTO);
    }

    public static ReservationCreateDto createCreateDto() {
        return new ReservationCreateDto(DEFAULT_FROM, DEFAULT_TO, DEFAULT_GAME_TYPE, DEFAULT_USER_CREATE_DTO, DEFAULT_COURT_DTO);
    }
}
