package com.tennisclub.reservations.mapper;

import com.tennisclub.reservations.model.entity.BaseEntity;

/**
 * GenericMapper interface for mapping between entities and various types of DTOs.
 *
 * @param <TEntity> the type of the Entity
 * @param <TDto> the type of the main Data Transfer Object
 * @param <TCreateDto> the type of DTO used for creating a new entity
 * @param <TUpdateDto> the type of DTO used for updating an existing entity
 */
public interface GenericMapper<TEntity extends BaseEntity, TDto, TCreateDto, TUpdateDto> {

    /**
     * Converts a TCreateDto to an Entity.
     *
     * @param dto the TCreateDto to convert
     * @return the corresponding Entity
     */
    TEntity toEntityFromCreateDto(TCreateDto dto);

    /**
     * Converts a TUpdateDto to an Entity.
     *
     * @param dto the TUpdateDto to convert
     * @return the corresponding Entity
     */
    TEntity toEntityFromUpdateDto(TUpdateDto dto);

    /**
     * Converts an Entity to a TDto.
     *
     * @param entity the Entity to convert
     * @return the corresponding TDto
     */
    TDto toDto(TEntity entity);
}
