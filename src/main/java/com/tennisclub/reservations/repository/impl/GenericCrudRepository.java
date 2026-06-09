package com.tennisclub.reservations.repository.impl;

import com.tennisclub.reservations.model.entity.BaseEntity;
import com.tennisclub.reservations.repository.CrudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
public abstract class GenericCrudRepository<TEntity extends BaseEntity> implements CrudRepository<TEntity> {

    private final Class<TEntity> type;

    @PersistenceContext
    private EntityManager entityManager;

    public GenericCrudRepository(Class<TEntity> type) {
        this.type = type;
    }

    @Override
    @Transactional
    public TEntity save(TEntity newEntity) {
        log.info("saving entity {}", newEntity);

        if (newEntity.getId() == null) {
            entityManager.persist(newEntity);
            return newEntity;
        }

        return entityManager.merge(newEntity);
    }

    @Override
    @Transactional
    public TEntity update(TEntity entity) {
        log.info("updating entity {}", entity);

        return entityManager.merge(entity);
    }

    @Override
    public Optional<TEntity> findById(Long id) {
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
    public Page<TEntity> findAll(Pageable pageable) {
        log.info("finding all entities for pageable");

        var query = entityManager.createQuery(getNotDeletedQuery());
        applyPagination(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, countNotDeleted());
    }

    @Override
    @Transactional
    public Optional<TEntity> softDeleteById(Long id) {
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

        var query = entityManager.createQuery(getNotDeletedQuery());
        applyPagination(query, pageable);

        var entities = query.getResultList();

        for (var entity : entities) {
            entity.softDelete();
            save(entity);
        }
    }

    private CriteriaQuery<TEntity> getNotDeletedQuery() {
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

    private Predicate isNotDeleted(CriteriaBuilder cb, Root<TEntity> root) {
        return cb.equal(root.get(BaseEntity.FIELD_DELETED), false);
    }

    private void applyPagination(TypedQuery<TEntity> query, Pageable pageable) {
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
    }
}
