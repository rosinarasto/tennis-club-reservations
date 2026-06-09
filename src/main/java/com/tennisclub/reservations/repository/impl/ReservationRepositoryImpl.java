package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReservationRepositoryImpl extends GenericCrudRepository<Reservation> implements ReservationRepository {

    @PersistenceContext
    private EntityManager em;

    public ReservationRepositoryImpl() {
        super(Reservation.class);
    }

    @Override
    public boolean isDateAvailable(int number, LocalDateTime from, LocalDateTime to) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var court = cq.from(Court.class);

        var overlappingReservation = cq.subquery(Long.class);
        var reservation = overlappingReservation.from(Reservation.class);

        overlappingReservation.select(cb.literal(1L))
                .where(
                        cb.equal(reservation.get("court"), court),
                        cb.equal(reservation.get("deleted"), false),
                        cb.greaterThan(reservation.get("to"), from),
                        cb.lessThan(reservation.get("from"), to)
                );

        cq.select(cb.count(court))
                .where(
                        cb.equal(court.get("number"), number),
                        cb.equal(court.get("deleted"), false),
                        cb.not(cb.exists(overlappingReservation))
                );

        return em.createQuery(cq).getSingleResult() > 0;
    }

    @Override
    public List<Reservation> findByCourtNumberOrderByCreationDate(int number) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Reservation.class);
        var root = cq.from(Reservation.class);
        var court = root.join("court");

        cq.select(root)
                .where(
                        cb.equal(court.get("number"), number),
                        cb.equal(court.get("deleted"), false),
                        cb.equal(root.get("deleted"), false)
                )
                .orderBy(cb.asc(root.get("creationDate")));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Reservation> findByUserPhoneNumberOrderByFrom(String phoneNumber, boolean future, LocalDateTime now) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Reservation.class);
        var root = cq.from(Reservation.class);
        var user = root.join("user");

        var predicate = cb.and(
                cb.equal(user.get("phoneNumber"), phoneNumber),
                cb.equal(user.get("deleted"), false),
                cb.equal(root.get("deleted"), false)
        );

        if (future) {
            predicate = cb.and(predicate, cb.greaterThan(root.get("from"), now));
        }

        cq.select(root)
                .where(predicate)
                .orderBy(cb.asc(root.get("from")));

        return em.createQuery(cq).getResultList();
    }
}
