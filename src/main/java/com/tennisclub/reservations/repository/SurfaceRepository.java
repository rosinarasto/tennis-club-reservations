package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.Surface;

import java.util.Optional;

/**
 * Provides persistence operations for court surfaces.
 */
public interface SurfaceRepository extends CrudRepository<Surface> {

    /**
     * Finds a non-deleted surface by name.
     *
     * @param name surface name.
     * @return optional with found surface, or empty optional if no surface with given name is found.
     */
    Optional<Surface> findByName(String name);
}
