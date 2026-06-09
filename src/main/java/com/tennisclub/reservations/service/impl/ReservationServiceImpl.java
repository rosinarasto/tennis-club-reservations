package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.model.entity.Reservation;
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
    public Reservation create(Reservation reservation) {
        log.info("Creating new reservation {}", reservation);

        var user = userService.getOrCreate(reservation.getUser());
        var courtNumber = reservation.getCourt().getNumber();
        var court = courtService.findByNumber(courtNumber);

        reservation.setUser(user);
        reservation.setCourt(court);

        return reservationRepository.save(reservation);
    }

    @Override
    public boolean isDateAvailable(int number, LocalDateTime from, LocalDateTime to) {
        log.info("isDateAvailable for court number {} from {} to {}", number, from, to);

        return reservationRepository.isDateAvailable(number, from, to);
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
}
