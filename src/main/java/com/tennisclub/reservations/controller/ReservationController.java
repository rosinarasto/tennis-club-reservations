package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.mapper.ReservationMapper;
import com.tennisclub.reservations.model.dto.PaginatedResponse;
import com.tennisclub.reservations.model.dto.ReservationCreatedResponseDto;
import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.model.dto.update.ReservationUpdateDto;
import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.security.annotation.RequiredRoles;
import com.tennisclub.reservations.service.ReservationService;
import com.tennisclub.reservations.util.PriceCalculationUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequiredRoles({Role.USER, Role.ADMIN})
    @PostMapping
    public ResponseEntity<ReservationCreatedResponseDto> createReservation(@Valid @RequestBody ReservationCreateDto reservationCreateDto) {
        var reservation = reservationService.create(reservationCreateDto);
        var price = PriceCalculationUtil.calculatePrice(reservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ReservationCreatedResponseDto(reservation.getId(), price));
    }

    @RequiredRoles(Role.ADMIN)
    @PutMapping
    public ResponseEntity<ReservationDto> updateReservation(@Valid @RequestBody ReservationUpdateDto updateDto) {
        return ResponseEntity.ok(reservationMapper.toDto(reservationService.update(updateDto)));
    }

    @RequiredRoles(Role.ADMIN)
    @DeleteMapping(ApiUris.ID_URI)
    public ResponseEntity<Void> deleteReservation(@PathVariable long id) {
        reservationService.softDeleteById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));
        return ResponseEntity.noContent().build();
    }

    @RequiredRoles({Role.USER, Role.ADMIN})
    @GetMapping
    public ResponseEntity<PaginatedResponse<ReservationDto>> getReservations(Pageable pageable) {
        var reservations = reservationService.findAll(pageable).map(reservationMapper::toDto);
        return ResponseEntity.ok(PaginatedResponse.from(reservations));
    }

    @RequiredRoles({Role.USER, Role.ADMIN})
    @GetMapping(ApiUris.ID_URI)
    public ResponseEntity<ReservationDto> getReservation(@PathVariable long id) {
        return reservationService.findById(id)
                .map(reservationMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));
    }
}
