package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.mapper.SurfaceMapper;
import com.tennisclub.reservations.model.dto.PaginatedResponse;
import com.tennisclub.reservations.model.dto.SurfaceDto;
import com.tennisclub.reservations.model.dto.create.SurfaceCreateDto;
import com.tennisclub.reservations.security.annotation.AdminOnly;
import com.tennisclub.reservations.security.annotation.UserOrAdmin;
import com.tennisclub.reservations.service.SurfaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @AdminOnly
    @PostMapping
    public ResponseEntity<SurfaceDto> createSurface(@Valid @RequestBody SurfaceCreateDto createDto) {
        var surface = surfaceMapper.toEntityFromCreateDto(createDto);
        return ResponseEntity.ok(surfaceMapper.toDto(surfaceService.create(surface)));
    }

    @AdminOnly
    @PutMapping
    public ResponseEntity<SurfaceDto> updateSurface(@Valid @RequestBody SurfaceDto updateDto) {
        var surface = surfaceMapper.toEntityFromUpdateDto(updateDto);
        return ResponseEntity.ok(surfaceMapper.toDto(surfaceService.update(surface)));
    }

    @AdminOnly
    @DeleteMapping
    public ResponseEntity<Void> deleteSurfaces(Pageable pageable) {
        surfaceService.softDeleteAll(pageable);
        return ResponseEntity.ok().build();
    }

    @AdminOnly
    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<SurfaceDto> deleteSurface(@PathVariable long id) {
        var surface = surfaceService.softDeleteById(id);
        return surface.map(surfaceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @UserOrAdmin
    @GetMapping
    public ResponseEntity<PaginatedResponse<SurfaceDto>> getSurfaces(Pageable pageable) {
        var surfaces = surfaceService.findAll(pageable).map(surfaceMapper::toDto);
        return ResponseEntity.ok(PaginatedResponse.from(surfaces));
    }

    @UserOrAdmin
    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<SurfaceDto> getSurface(@PathVariable long id) {
        var surface = surfaceService.findById(id);
        return surface.map(surfaceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
