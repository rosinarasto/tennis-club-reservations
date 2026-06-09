package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.entity.Surface;
import com.tennisclub.reservations.repository.SurfaceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class SurfaceRepositoryImpl extends GenericCrudRepository<Surface> implements SurfaceRepository {

    @PersistenceContext
    private EntityManager em;

    public SurfaceRepositoryImpl() {
        super(Surface.class);
    }

    @Override
    public Optional<Surface> findByName(String name) {
        log.info("Find surface by name: {}", name);

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Surface.class);
        var root = cq.from(Surface.class);

        cq.select(root).where(cb.equal(root.get("name"), name), cb.equal(root.get("deleted"), false));

        try {
            return Optional.of(em.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
