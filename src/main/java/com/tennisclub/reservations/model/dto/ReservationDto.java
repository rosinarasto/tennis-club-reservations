package com.tennisclub.reservations.model.dto;

import com.tennisclub.reservations.model.GameType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto extends BaseDto {

    @FutureOrPresent
    @NotNull
    private LocalDateTime from;

    @Future
    @NotNull
    private LocalDateTime to;

    @NotNull
    private GameType gameType;

    @NotNull
    private UserDto user;

    @NotNull
    private CourtDto court;
}
