package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.mapper.ReservationMapper;
import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
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
    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    @PostMapping
    public ResponseEntity<BigDecimal> createReservation(@Valid @RequestBody ReservationCreateDto reservationCreateDto) {
        var reservation = reservationService.create(reservationMapper.toEntityFromCreateDto(reservationCreateDto));
        var price = PriceCalculationUtil.calculatePrice(reservation);
        return ResponseEntity.ok().body(price);
    }

    @PutMapping
    public ResponseEntity<ReservationDto> updateReservation(@Valid @RequestBody ReservationDto updateDto) {
        var reservation = reservationMapper.toEntityFromUpdateDto(updateDto);
        return ResponseEntity.ok(reservationMapper.toDto(reservationService.update(reservation)));
    }

    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<ReservationDto> deleteReservation(@PathVariable long id) {
        var reservation = reservationService.softDeleteById(id);
        return reservation.map(reservationMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDto>> getReservations(Pageable pageable) {
        return ResponseEntity.ok(reservationService.findAll(pageable).map(reservationMapper::toDto));
    }

    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<ReservationDto> getReservation(@PathVariable long id) {
        var reservation = reservationService.findById(id);
        return reservation.map(reservationMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
