package com.tennisclub.reservations.mapper;

import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.model.entity.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends GenericMapper<Reservation, ReservationDto, ReservationDto> {

    Reservation toEntityFromCreateDto(ReservationCreateDto dto);
}
