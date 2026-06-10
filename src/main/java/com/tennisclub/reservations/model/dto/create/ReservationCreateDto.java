package com.tennisclub.reservations.model.dto.create;

import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.validator.annotation.AvailableDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a reservation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@AvailableDate
public class ReservationCreateDto {

    @FutureOrPresent
    @NotNull
    private LocalDateTime from;

    @Future
    @NotNull
    private LocalDateTime to;

    @NotNull
    private GameType gameType;

    @NotNull
    private UserCreateDto user;

    @NotNull
    @Positive
    private Long courtId;
}
