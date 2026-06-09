package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Represents a service for any entity.
 *
 * @param <TModel> the type of the entity
 */
public interface CrudService<TModel extends BaseEntity> {

    /**
     * Update given {@code entity}.
     *
     * @return updated entity
     */
    TModel update(TModel entity);

    /**
     * Find entity with given {@code id}.
     *
     * @return optional with found entity, or empty optional if no entity with given {@code id} is found
     */
    Optional<TModel> findById(Long id);

    /**
     * Find all entities.
     */
    Page<TModel> findAll(Pageable pageable);

    /**
     * Soft delete entity with given {@code id}.
     *
     * @return the deleted entity with id.
     */
    Optional<TModel> softDeleteById(Long id);

    /**
     * Soft delete all entities.
     */
    void softDeleteAll(Pageable pageable);
}
