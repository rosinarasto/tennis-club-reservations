package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.dto.create.CourtCreateDto;
import com.tennisclub.reservations.model.dto.update.CourtUpdateDto;
import com.tennisclub.reservations.model.entity.Court;

/**
 * Defines court-specific business operations.
 */
public interface CourtService extends CrudService<Court> {

    /**
     * Creates a new court.
     *
     * @param createDto court data to create.
     * @return created court.
     */
    Court create(CourtCreateDto createDto);

    /**
     * Updates an existing court.
     *
     * @param updateDto court data to update.
     * @return updated court.
     */
    Court update(CourtUpdateDto updateDto);

    /**
     * Finds a court by its court number.
     *
     * @param number court number.
     * @return found court.
     */
    Court findByNumber(int number);
}
