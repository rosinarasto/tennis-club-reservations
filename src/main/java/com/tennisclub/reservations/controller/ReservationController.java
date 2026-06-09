package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.dto.ReservationDto;
import com.tennisclub.reservations.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.service.ReservationService;
import com.tennisclub.reservations.util.PriceCalculationUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiUris.RESERVATION_URI)
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService service) {
        this.reservationService = service;
    }

    @PostMapping
    public ResponseEntity<BigDecimal> createReservation(@Valid @RequestBody ReservationCreateDto reservationCreateDto) {
        var reservation = reservationService.create(reservationCreateDto);
        var price = PriceCalculationUtil.calculatePrice(reservation);
        return ResponseEntity.ok().body(price);
    }

    @PutMapping
    public ResponseEntity<ReservationDto> updateReservation(@Valid @RequestBody ReservationDto updateDto) {
        return ResponseEntity.ok(reservationService.update(updateDto));
    }

    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<ReservationDto> deleteReservation(@PathVariable long id) {
        var reservation = reservationService.softDeleteById(id);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDto>> getReservations(Pageable pageable) {
        return ResponseEntity.ok(reservationService.findAll(pageable));
    }

    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<ReservationDto> getReservation(@PathVariable long id) {
        var reservation = reservationService.findById(id);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
