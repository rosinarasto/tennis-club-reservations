package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.dto.CourtDto;
import com.tennisclub.reservations.dto.ReservationDto;
import com.tennisclub.reservations.dto.create.CourtCreateDto;
import com.tennisclub.reservations.service.CourtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiUris.COURT_URI)
public class CourtController {

    private final CourtService courtService;

    @Autowired
    public CourtController(CourtService service) {
        this.courtService = service;
    }

    @PostMapping
    public ResponseEntity<CourtDto> createCourt(@Valid @RequestBody CourtCreateDto createDto) {
        return ResponseEntity.ok(courtService.create(createDto));
    }

    @PutMapping
    public ResponseEntity<CourtDto> updateCourt(@Valid @RequestBody CourtDto updateDto) {
        return ResponseEntity.ok(courtService.update(updateDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCourts(Pageable pageable) {
        courtService.softDeleteAll(pageable);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<CourtDto> deleteCourt(@PathVariable long id) {
        var court = courtService.softDeleteById(id);
        return court.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<Page<CourtDto>> getCourts(Pageable pageable) {
        return ResponseEntity.ok(courtService.findAll(pageable));
    }

    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<CourtDto> getCourt(@PathVariable long id) {
        var court = courtService.findById(id);
        return court.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping(ApiUris.COURT_RESERVATIONS_URI)
    public ResponseEntity<List<ReservationDto>> getCourtReservations(@PathVariable int number) {
        var reservations = courtService.findReservations(number);
        return ResponseEntity.ok().body(reservations);
    }
}
