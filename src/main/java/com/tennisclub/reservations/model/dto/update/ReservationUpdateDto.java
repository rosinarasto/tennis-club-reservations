package com.tennisclub.reservations.model.dto.update;

import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.model.dto.BaseDto;
import com.tennisclub.reservations.model.dto.create.UserCreateDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for updating a reservation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateDto extends BaseDto {

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
