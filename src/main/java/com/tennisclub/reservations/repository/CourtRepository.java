package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.Court;

import java.util.Optional;

/**
 * Provides persistence operations for courts.
 */
public interface CourtRepository extends CrudRepository<Court> {

    /**
     * Finds a non-deleted court by its court number.
     *
     * @param number court number.
     * @return optional with found court, or empty optional if no court with given number is found.
     */
    Optional<Court> findByCourtNumber(int number);
}
