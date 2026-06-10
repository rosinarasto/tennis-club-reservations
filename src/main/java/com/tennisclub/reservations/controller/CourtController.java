package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.mapper.CourtMapper;
import com.tennisclub.reservations.mapper.ReservationMapper;
import com.tennisclub.reservations.model.dto.CourtDto;
import com.tennisclub.reservations.model.dto.PaginatedResponse;
import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.create.CourtCreateDto;
import com.tennisclub.reservations.model.dto.update.CourtUpdateDto;
import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.security.annotation.RequiredRoles;
import com.tennisclub.reservations.service.CourtService;
import com.tennisclub.reservations.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiUris.COURT_URI)
public class CourtController {

    private final CourtService courtService;
    private final ReservationService reservationService;
    private final CourtMapper courtMapper;
    private final ReservationMapper reservationMapper;

    @Autowired
    public CourtController(
            CourtService courtService,
            ReservationService reservationService,
            CourtMapper courtMapper,
            ReservationMapper reservationMapper
    ) {
        this.courtService = courtService;
        this.reservationService = reservationService;
        this.courtMapper = courtMapper;
        this.reservationMapper = reservationMapper;
    }

    @RequiredRoles(Role.ADMIN)
    @PostMapping
    public ResponseEntity<CourtDto> createCourt(@Valid @RequestBody CourtCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courtMapper.toDto(courtService.create(createDto)));
    }

    @RequiredRoles(Role.ADMIN)
    @PutMapping
    public ResponseEntity<CourtDto> updateCourt(@Valid @RequestBody CourtUpdateDto updateDto) {
        return ResponseEntity.ok(courtMapper.toDto(courtService.update(updateDto)));
    }

    @RequiredRoles(Role.ADMIN)
    @DeleteMapping
    public ResponseEntity<Void> deleteCourts(Pageable pageable) {
        courtService.softDeleteAll(pageable);
        return ResponseEntity.noContent().build();
    }

    @RequiredRoles(Role.ADMIN)
    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<Void> deleteCourt(@PathVariable long id) {
        courtService.softDeleteById(id)
                .orElseThrow(() -> new NotFoundException("Court with id " + id + " not found"));
        return ResponseEntity.noContent().build();
    }

    @RequiredRoles({Role.USER, Role.ADMIN})
    @GetMapping
    public ResponseEntity<PaginatedResponse<CourtDto>> getCourts(Pageable pageable) {
        var courts = courtService.findAll(pageable).map(courtMapper::toDto);
        return ResponseEntity.ok(PaginatedResponse.from(courts));
    }

    @RequiredRoles({Role.USER, Role.ADMIN})
    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<CourtDto> getCourt(@PathVariable long id) {
        return courtService.findById(id)
                .map(courtMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Court with id " + id + " not found"));
    }

    @RequiredRoles({Role.USER, Role.ADMIN})
    @GetMapping(ApiUris.COURT_RESERVATIONS_URI)
    public ResponseEntity<List<ReservationDto>> getCourtReservations(@PathVariable int number) {
        var reservations = reservationService.findByCourtNumber(number);
        return ResponseEntity.ok().body(reservations.stream().map(reservationMapper::toDto).toList());
    }
}
