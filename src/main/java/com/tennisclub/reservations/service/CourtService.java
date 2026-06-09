package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Court;

/**
 * Defines court-specific business operations.
 */
public interface CourtService extends CrudService<Court> {

    /**
     * Creates a new court.
     *
     * @param newCourt court to create.
     * @return created court.
     */
    Court create(Court newCourt);

    /**
     * Finds a court by its court number.
     *
     * @param number court number.
     * @return found court.
     */
    Court findByNumber(int number);
}
