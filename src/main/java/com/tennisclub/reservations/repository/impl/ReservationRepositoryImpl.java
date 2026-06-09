package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.entity.BaseEntity;
import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.entity.User;
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
                        cb.equal(reservation.get(Reservation.FIELD_COURT), court),
                        cb.equal(reservation.get(BaseEntity.FIELD_DELETED), false),
                        cb.greaterThan(reservation.get(Reservation.FIELD_TO), from),
                        cb.lessThan(reservation.get(Reservation.FIELD_FROM), to)
                );

        cq.select(cb.count(court))
                .where(
                        cb.equal(court.get(Court.FIELD_NUMBER), number),
                        cb.equal(court.get(BaseEntity.FIELD_DELETED), false),
                        cb.not(cb.exists(overlappingReservation))
                );

        return em.createQuery(cq).getSingleResult() > 0;
    }

    @Override
    public List<Reservation> findByCourtNumberOrderByCreationDate(int number) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Reservation.class);
        var root = cq.from(Reservation.class);
        var court = root.join(Reservation.FIELD_COURT);

        cq.select(root)
                .where(
                        cb.equal(court.get(Court.FIELD_NUMBER), number),
                        cb.equal(court.get(BaseEntity.FIELD_DELETED), false),
                        cb.equal(root.get(BaseEntity.FIELD_DELETED), false)
                )
                .orderBy(cb.asc(root.get(BaseEntity.FIELD_CREATION_DATE)));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Reservation> findByUserPhoneNumberOrderByFrom(String phoneNumber, boolean future, LocalDateTime now) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Reservation.class);
        var root = cq.from(Reservation.class);
        var user = root.join(Reservation.FIELD_USER);

        var predicate = cb.and(
                cb.equal(user.get(User.FIELD_PHONE_NUMBER), phoneNumber),
                cb.equal(user.get(BaseEntity.FIELD_DELETED), false),
                cb.equal(root.get(BaseEntity.FIELD_DELETED), false)
        );

        if (future) {
            predicate = cb.and(predicate, cb.greaterThan(root.get(Reservation.FIELD_FROM), now));
        }

        cq.select(root)
                .where(predicate)
                .orderBy(cb.asc(root.get(Reservation.FIELD_FROM)));

        return em.createQuery(cq).getResultList();
    }
}
