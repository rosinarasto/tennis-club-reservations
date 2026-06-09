package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Defines reservation-specific business operations.
 */
public interface ReservationService extends CrudService<Reservation> {

    /**
     * Creates a new reservation with a managed court and user.
     *
     * @param reservation reservation to create.
     * @return created reservation.
     */
    Reservation create(Reservation reservation);

    /**
     * Checks whether the requested court is available for the given time interval.
     *
     * @param number court number.
     * @param from reservation start.
     * @param to reservation end.
     * @return {@code true} when the court exists and has no overlapping reservation.
     */
    boolean isDateAvailable(int number, LocalDateTime from, LocalDateTime to);

    /**
     * Finds reservations for the given court number.
     *
     * @param number court number.
     * @return reservations ordered by creation date.
     */
    List<Reservation> findByCourtNumber(int number);

    /**
     * Finds reservations for the given user phone number.
     *
     * @param phoneNumber user phone number.
     * @param future whether only future reservations should be returned.
     * @return reservations ordered by reservation start.
     */
    List<Reservation> findByUserPhoneNumber(String phoneNumber, boolean future);
}
