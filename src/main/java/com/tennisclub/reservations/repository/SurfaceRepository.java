package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.Surface;

import java.util.Optional;

public interface SurfaceRepository extends CrudRepository<Surface> {

    Optional<Surface> findByName(String name);
}
