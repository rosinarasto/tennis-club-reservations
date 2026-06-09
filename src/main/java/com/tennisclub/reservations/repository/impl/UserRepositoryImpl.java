package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.User;
import com.tennisclub.reservations.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl extends GenericCrudRepository<User> implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    public UserRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        log.info("find user by phone number");

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(User.class);
        var root = cq.from(User.class);

        cq.select(root).where(cb.equal(root.get("phoneNumber"), phoneNumber), cb.equal(root.get("deleted"), false));

        try {
            return Optional.of(em.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByName(String name) {
        log.info("find user by name");

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(User.class);
        var root = cq.from(User.class);

        cq.select(root).where(cb.equal(root.get("name"), name), cb.equal(root.get("deleted"), false));

        return em.createQuery(cq)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
