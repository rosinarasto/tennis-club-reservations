package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation> {

    boolean isDateAvailable(int number, LocalDateTime from, LocalDateTime to);

    List<Reservation> findByCourtNumberOrderByCreationDate(int number);

    List<Reservation> findByUserPhoneNumberOrderByFrom(String phoneNumber, boolean future, LocalDateTime now);
}
