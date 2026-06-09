package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Represents a repository for any entity.
 *
 * @param <T> the type of the entity.
 */
public interface CrudRepository<T extends BaseEntity> {

    /**
     * Persist given {@code newEntity}.
     *
     * @return the persisted entity with generated id.
     */
    T save(T newEntity);


    /**
     * Update given {@code entity}.
     *
     * @return the updated entity.
     */
    T update(T entity);

    /**
     * Find entity with given {@code id}.
     *
     * @return optional with found entity, or empty optional if no entity with given {@code id} is found
     */
    Optional<T> findById(Long id);

    /**
     * Find all entities for pageable.
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Soft delete entity with given {@code id}.
     */
    Optional<T> softDeleteById(Long id);

    /**
     * Soft delete all entities from pageable.
     */
    void softDeleteAll(Pageable pageable);
}
