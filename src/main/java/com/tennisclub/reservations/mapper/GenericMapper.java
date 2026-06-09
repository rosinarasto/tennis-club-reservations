package com.tennisclub.reservations.mapper;

import com.tennisclub.reservations.model.entity.BaseEntity;

/**
 * Defines common mapping operations between entities and DTOs.
 *
 * @param <TEntity> the type of the entity.
 * @param <TDto> the type of the main DTO.
 * @param <TCreateDto> the type of DTO used for creating a new entity.
 * @param <TUpdateDto> the type of DTO used for updating an existing entity.
 */
public interface GenericMapper<TEntity extends BaseEntity, TDto, TCreateDto, TUpdateDto> {

    /**
     * Converts a create DTO to an entity.
     *
     * @param dto create DTO to convert.
     * @return corresponding entity.
     */
    TEntity toEntityFromCreateDto(TCreateDto dto);

    /**
     * Converts an update DTO to an entity.
     *
     * @param dto update DTO to convert.
     * @return corresponding entity.
     */
    TEntity toEntityFromUpdateDto(TUpdateDto dto);

    /**
     * Converts an entity to a DTO.
     *
     * @param entity entity to convert.
     * @return corresponding DTO.
     */
    TDto toDto(TEntity entity);
}
