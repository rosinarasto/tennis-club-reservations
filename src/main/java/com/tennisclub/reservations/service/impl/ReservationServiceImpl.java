package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.dto.ReservationDto;
import com.tennisclub.reservations.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.mapper.ReservationMapper;
import com.tennisclub.reservations.model.Reservation;
import com.tennisclub.reservations.model.User;
import com.tennisclub.reservations.repository.CourtRepository;
import com.tennisclub.reservations.repository.ReservationRepository;
import com.tennisclub.reservations.repository.UserRepository;
import com.tennisclub.reservations.service.ReservationService;
import com.tennisclub.reservations.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class ReservationServiceImpl extends GenericCrudService<Reservation, ReservationDto, ReservationCreateDto, ReservationDto> implements ReservationService {

    private final UserService userService;

    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationMapper mapper,
                                  UserRepository userRepository, UserService userService,
                                  CourtRepository courtRepository) {
        super(reservationRepository, mapper, ReservationDto.class, Reservation.class);
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.reservationMapper = mapper;
        this.courtRepository = courtRepository;
    }

    @Override
    public ReservationDto create(ReservationCreateDto createDto) {
        log.info("Creating new reservation {}", createDto);

        var user = getOrCreateUser(createDto);
        var court = courtRepository.findByCourtNumber(createDto.getCourt().getNumber())
                .orElseThrow(() -> new NotFoundException("Court with number " + createDto.getCourt().getNumber() + " not found"));

        var reservation = reservationMapper.toEntityFromCreateDto(createDto);
        reservation.setUser(user);
        reservation.setCourt(court);

        reservation = reservationRepository.save(reservation);

        user.getReservations().add(reservation);
        court.getReservations().add(reservation);

        return reservationMapper.toDto(reservation);
    }

    public boolean isDateAvailable(int number, LocalDateTime from, LocalDateTime to) {
        log.info("isDateAvailable for court number {} from {} to {}", number, from, to);

        var court = courtRepository.findByCourtNumber(number);

        return court.map(value -> value.getReservations().stream()
                .filter(res -> !res.isDeleted())
                .noneMatch(res -> from.isBefore(res.getTo()) && to.isAfter(res.getFrom())))
                .orElse(false);
    }

    private User getOrCreateUser(ReservationCreateDto createDto) {
        var phoneNumber = createDto.getUser().getPhoneNumber();

        var user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent()) {
            return user.get();
        }

        userService.create(createDto.getUser());
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("User with phone number " + phoneNumber + " not found"));
    }
}
