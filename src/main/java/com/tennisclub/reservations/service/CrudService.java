package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Represents a service for any entity.
 *
 * @param <TEntity> the type of the entity
 */
public interface CrudService<TEntity extends BaseEntity> {

    /**
     * Update given {@code entity}.
     *
     * @return updated entity
     */
    TEntity update(TEntity entity);

    /**
     * Find entity with given {@code id}.
     *
     * @return optional with found entity, or empty optional if no entity with given {@code id} is found
     */
    Optional<TEntity> findById(Long id);

    /**
     * Find all entities.
     */
    Page<TEntity> findAll(Pageable pageable);

    /**
     * Soft delete entity with given {@code id}.
     *
     * @return the deleted entity with id.
     */
    Optional<TEntity> softDeleteById(Long id);

    /**
     * Soft delete all entities.
     */
    void softDeleteAll(Pageable pageable);
}
