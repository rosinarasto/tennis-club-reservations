package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Surface;

public interface SurfaceService extends CrudService<Surface> {

    Surface create(Surface newSurface);
}
