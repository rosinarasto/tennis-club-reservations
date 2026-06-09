package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.entity.BaseEntity;
import com.tennisclub.reservations.repository.CrudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
@Slf4j
public abstract class GenericCrudRepository<T extends BaseEntity> implements CrudRepository<T> {

    private final Class<T> type;

    @PersistenceContext
    private EntityManager entityManager;

    public GenericCrudRepository(Class<T> type) {
        this.type = type;
    }

    @Override
    @Transactional
    public T save(T newEntity) {
        log.info("saving entity {}", newEntity);

        if (newEntity.getId() == null) {
            entityManager.persist(newEntity);
            return newEntity;
        }

        return entityManager.merge(newEntity);
    }

    @Override
    @Transactional
    public T update(T entity) {
        log.info("updating entity {}", entity);

        return entityManager.merge(entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        log.info("finding entity with id {}", id);

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(type);
        var root = cq.from(type);

        cq.select(root).where(
                cb.equal(root.get(BaseEntity.FIELD_ID), id),
                cb.equal(root.get(BaseEntity.FIELD_DELETED), false)
        );

        try {
            return Optional.of(entityManager.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        log.info("finding all entities for pageable");

        var q = entityManager.createQuery(getNotDeleted());

        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(q.getResultList(), pageable, countNotDeleted());
    }

    @Override
    @Transactional
    public Optional<T> softDeleteById(Long id) {
        log.info("deleting entity with id {}", id);

        var entity = findById(id);
        if (entity.isPresent()) {
            entity.get().softDelete();
            save(entity.get());
        }

        return entity;
    }


    @Override
    @Transactional
    public void softDeleteAll(Pageable pageable) {
        log.info("deleting all entities for pageable");

        var q = entityManager.createQuery(getNotDeleted());
        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());

        var entities = q.getResultList();

        for (var entity : entities) {
            entity.softDelete();
            save(entity);
        }

    }

    private CriteriaQuery<T> getNotDeleted() {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(type);
        var root = cq.from(type);

        return cq.where(isNotDeleted(cb, root));
    }

    private long countNotDeleted() {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var root = cq.from(type);

        cq.select(cb.count(root))
                .where(isNotDeleted(cb, root));

        return entityManager.createQuery(cq).getSingleResult();
    }

    private Predicate isNotDeleted(CriteriaBuilder cb, Root<T> root) {
        return cb.equal(root.get(BaseEntity.FIELD_DELETED), false);
    }
}
