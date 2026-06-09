package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.mapper.SurfaceMapper;
import com.tennisclub.reservations.model.dto.SurfaceDto;
import com.tennisclub.reservations.model.dto.create.SurfaceCreateDto;
import com.tennisclub.reservations.service.SurfaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiUris.SURFACE_URI)
public class SurfaceController {

    private final SurfaceService surfaceService;
    private final SurfaceMapper surfaceMapper;

    @Autowired
    public SurfaceController(SurfaceService surfaceService, SurfaceMapper surfaceMapper) {
        this.surfaceService = surfaceService;
        this.surfaceMapper = surfaceMapper;
    }

    @PostMapping
    public ResponseEntity<SurfaceDto> createSurface(@Valid @RequestBody SurfaceCreateDto createDto) {
        var surface = surfaceMapper.toEntityFromCreateDto(createDto);
        return ResponseEntity.ok(surfaceMapper.toDto(surfaceService.create(surface)));
    }

    @PutMapping
    public ResponseEntity<SurfaceDto> updateSurface(@Valid @RequestBody SurfaceDto updateDto) {
        var surface = surfaceMapper.toEntityFromUpdateDto(updateDto);
        return ResponseEntity.ok(surfaceMapper.toDto(surfaceService.update(surface)));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSurfaces(Pageable pageable) {
        surfaceService.softDeleteAll(pageable);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<SurfaceDto> deleteSurface(@PathVariable long id) {
        var surface = surfaceService.softDeleteById(id);
        return surface.map(surfaceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<Page<SurfaceDto>> getSurfaces(Pageable pageable) {
        return ResponseEntity.ok(surfaceService.findAll(pageable).map(surfaceMapper::toDto));
    }

    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<SurfaceDto> getSurface(@PathVariable long id) {
        var surface = surfaceService.findById(id);
        return surface.map(surfaceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
