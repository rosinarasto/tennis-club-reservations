package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Surface;

/**
 * Defines surface-specific business operations.
 */
public interface SurfaceService extends CrudService<Surface> {

    /**
     * Creates a new court surface.
     *
     * @param newSurface surface to create.
     * @return created surface.
     */
    Surface create(Surface newSurface);
}
