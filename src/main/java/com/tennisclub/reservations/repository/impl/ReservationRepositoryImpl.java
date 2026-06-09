package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationRepositoryImpl extends GenericCrudRepository<Reservation> implements ReservationRepository {

    public ReservationRepositoryImpl() {
        super(Reservation.class);
    }
}
