package com.tennisclub.reservations.model.dto.create;

import com.tennisclub.reservations.model.dto.CourtDto;
import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.validator.AvailableDate;
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
    private CourtDto court;
}
