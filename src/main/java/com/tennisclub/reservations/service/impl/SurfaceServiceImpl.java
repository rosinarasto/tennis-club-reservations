package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.entity.Surface;
import com.tennisclub.reservations.repository.SurfaceRepository;
import com.tennisclub.reservations.service.SurfaceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class SurfaceServiceImpl extends GenericCrudService<Surface> implements SurfaceService {

    private final SurfaceRepository surfaceRepository;

    @Autowired
    public SurfaceServiceImpl(SurfaceRepository surfaceRepository) {
        super(surfaceRepository);
        this.surfaceRepository = surfaceRepository;
    }

    @Override
    public Surface create(Surface newSurface) {
        log.info("Creating new surface: {}", newSurface);

        var surface = surfaceRepository.findByName(newSurface.getName());

        if (surface.isPresent()) {
            throw new ResourceAlreadyExistsException("surface with given name already exists");
        }

        return surfaceRepository.save(newSurface);
    }
}
