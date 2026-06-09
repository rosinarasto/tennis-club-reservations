package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService extends CrudService<Reservation> {

    Reservation create(Reservation reservation);

    boolean isDateAvailable(int number, LocalDateTime from, LocalDateTime to);

    List<Reservation> findByCourtNumber(int number);

    List<Reservation> findByUserPhoneNumber(String phoneNumber, boolean future);
}
