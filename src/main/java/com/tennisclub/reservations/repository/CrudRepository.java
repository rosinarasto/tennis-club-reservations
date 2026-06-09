package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Defines common repository operations for entities that support soft deletion.
 *
 * @param <TEntity> the type of the entity.
 */
public interface CrudRepository<TEntity extends BaseEntity> {

    /**
     * Persists the given {@code newEntity}.
     *
     * @param newEntity entity to persist.
     * @return the persisted entity with generated id.
     */
    TEntity save(TEntity newEntity);


    /**
     * Updates the given {@code entity}.
     *
     * @param entity entity with updated values.
     * @return the updated entity.
     */
    TEntity update(TEntity entity);

    /**
     * Finds an entity with the given {@code id}.
     *
     * @param id entity id.
     * @return optional with found entity, or empty optional if no entity with given {@code id} is found.
     */
    Optional<TEntity> findById(Long id);

    /**
     * Finds all non-deleted entities for the requested page.
     *
     * @param pageable requested page and sorting.
     * @return page with found entities.
     */
    Page<TEntity> findAll(Pageable pageable);

    /**
     * Soft deletes an entity with the given {@code id}.
     *
     * @param id entity id.
     * @return optional with deleted entity, or empty optional if no entity with given {@code id} is found.
     */
    Optional<TEntity> softDeleteById(Long id);

    /**
     * Soft deletes all non-deleted entities from the requested page.
     *
     * @param pageable requested page and sorting.
     */
    void softDeleteAll(Pageable pageable);
}
