package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.entity.BaseEntity;
import com.tennisclub.reservations.repository.CourtRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class CourtRepositoryImpl extends GenericCrudRepository<Court> implements CourtRepository {

    @PersistenceContext
    private EntityManager em;

    public CourtRepositoryImpl() {
        super(Court.class);
    }

    @Override
    public Optional<Court> findByCourtNumber(int number) {
        log.info("find court by its number");

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Court.class);
        var root = cq.from(Court.class);

        cq.select(root).where(
                cb.equal(root.get(Court.FIELD_NUMBER), number),
                cb.equal(root.get(BaseEntity.FIELD_DELETED), false)
        );

        try {
            return Optional.of(em.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
