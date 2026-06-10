package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.model.dto.create.UserCreateDto;
import com.tennisclub.reservations.model.dto.update.ReservationUpdateDto;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.entity.User;
import com.tennisclub.reservations.repository.ReservationRepository;
import com.tennisclub.reservations.service.CourtService;
import com.tennisclub.reservations.service.ReservationService;
import com.tennisclub.reservations.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ReservationServiceImpl extends GenericCrudService<Reservation> implements ReservationService {

    private final UserService userService;
    private final CourtService courtService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            UserService userService,
            CourtService courtService
    ) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.courtService = courtService;
    }

    @Override
    public Reservation create(ReservationCreateDto createDto) {
        log.info("Creating new reservation {}", createDto);

        var reservation = buildReservation(
                createDto.getFrom(),
                createDto.getTo(),
                createDto.getGameType(),
                createDto.getUser(),
                createDto.getCourtId()
        );

        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation update(ReservationUpdateDto updateDto) {
        log.info("Updating reservation {}", updateDto);

        var reservation = buildReservation(
                updateDto.getFrom(),
                updateDto.getTo(),
                updateDto.getGameType(),
                updateDto.getUser(),
                updateDto.getCourtId()
        );
        reservation.setId(updateDto.getId());

        return super.update(reservation);
    }

    @Override
    public boolean isDateAvailable(Long courtId, LocalDateTime from, LocalDateTime to) {
        log.info("isDateAvailable for court id {} from {} to {}", courtId, from, to);

        return reservationRepository.isDateAvailable(courtId, from, to);
    }

    @Override
    public List<Reservation> findByCourtNumber(int number) {
        log.info("Finding reservations for court number {}", number);

        return reservationRepository.findByCourtNumberOrderByCreationDate(number);
    }

    @Override
    public List<Reservation> findByUserPhoneNumber(String phoneNumber, boolean future) {
        log.info("Finding reservations for user phone number {}", phoneNumber);

        return reservationRepository.findByUserPhoneNumberOrderByFrom(phoneNumber, future, LocalDateTime.now());
    }

    private Reservation buildReservation(
            LocalDateTime from,
            LocalDateTime to,
            GameType gameType,
            UserCreateDto userDto,
            Long courtId
    ) {
        var userToCreate = new User();
        userToCreate.setName(userDto.getName());
        userToCreate.setPhoneNumber(userDto.getPhoneNumber());

        var user = userService.getOrCreate(userToCreate);
        var court = courtService.findById(courtId)
                .orElseThrow(() -> new NotFoundException("Court with id " + courtId + " not found"));

        return new Reservation(from, to, gameType, user, court);
    }
}
