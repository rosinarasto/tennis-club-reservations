package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.UserDto;
import com.tennisclub.reservations.model.dto.create.UserCreateDto;

import java.util.List;

public interface UserService extends CrudService<UserDto, UserCreateDto, UserDto> {

    List<ReservationDto> findReservations(String phoneNumber, boolean future);
}
