package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.mapper.ReservationMapper;
import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.security.annotation.RequiredRoles;
import com.tennisclub.reservations.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiUris.USER_URI)
public class UserController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @Autowired
    public UserController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    @RequiredRoles({Role.USER, Role.ADMIN})
    @GetMapping(ApiUris.USER_RESERVATIONS_URI)
    public ResponseEntity<List<ReservationDto>> getUserReservations(@PathVariable String phoneNumber, @RequestParam(required = false) boolean future) {
        var reservations = reservationService.findByUserPhoneNumber(phoneNumber, future);
        return ResponseEntity.ok().body(reservations.stream().map(reservationMapper::toDto).toList());
    }
}
