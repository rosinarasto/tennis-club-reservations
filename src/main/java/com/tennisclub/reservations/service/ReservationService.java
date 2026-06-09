package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;

import java.time.LocalDateTime;

public interface ReservationService extends CrudService<ReservationDto, ReservationCreateDto, ReservationDto> {

    boolean isDateAvailable(int number, LocalDateTime from, LocalDateTime to);
}
