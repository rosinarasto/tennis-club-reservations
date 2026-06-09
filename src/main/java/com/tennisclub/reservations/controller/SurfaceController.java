package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
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

    @Autowired
    public SurfaceController(SurfaceService surfaceService) {
        this.surfaceService = surfaceService;
    }

    @PostMapping
    public ResponseEntity<SurfaceDto> createSurface(@Valid @RequestBody SurfaceCreateDto createDto) {
        return ResponseEntity.ok(surfaceService.create(createDto));
    }

    @PutMapping
    public ResponseEntity<SurfaceDto> updateSurface(@Valid @RequestBody SurfaceDto updateDto) {
        return ResponseEntity.ok(surfaceService.update(updateDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSurfaces(Pageable pageable) {
        surfaceService.softDeleteAll(pageable);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<SurfaceDto> deleteSurface(@PathVariable long id) {
        var surface = surfaceService.softDeleteById(id);
        return surface.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<Page<SurfaceDto>> getSurfaces(Pageable pageable) {
        return ResponseEntity.ok(surfaceService.findAll(pageable));
    }

    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<SurfaceDto> getSurface(@PathVariable long id) {
        var surface = surfaceService.findById(id);
        return surface.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
