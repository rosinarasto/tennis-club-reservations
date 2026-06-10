package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides persistence operations for reservations.
 */
public interface ReservationRepository extends CrudRepository<Reservation> {

    /**
     * Checks whether the requested court is available for the given time interval.
     *
     * @param courtId court id.
     * @param from reservation start.
     * @param to reservation end.
     * @return {@code true} when the court exists and has no overlapping reservation.
     */
    boolean isDateAvailable(Long courtId, LocalDateTime from, LocalDateTime to);

    /**
     * Finds non-deleted reservations for the given court number.
     *
     * @param number court number.
     * @return reservations ordered by creation date.
     */
    List<Reservation> findByCourtNumberOrderByCreationDate(int number);

    /**
     * Finds non-deleted reservations for the given user phone number.
     *
     * @param phoneNumber user phone number.
     * @param future whether only future reservations should be returned.
     * @param now current time used when filtering future reservations.
     * @return reservations ordered by reservation start.
     */
    List<Reservation> findByUserPhoneNumberOrderByFrom(String phoneNumber, boolean future, LocalDateTime now);
}
