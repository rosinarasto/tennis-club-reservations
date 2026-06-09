package com.tennisclub.reservations.mapper;

import com.tennisclub.reservations.model.entity.BaseEntity;

/**
 * GenericMapper interface for mapping between entities and various types of DTOs.
 *
 * @param <TModel> the type of the Entity
 * @param <TDto> the type of the main Data Transfer Object
 * @param <TCreateDto> the type of the TDto used for creating a new entity
 * @param <TUpdateDto> the type of the TDto used for updating an existing entity
 */
public interface GenericMapper<TModel extends BaseEntity, TDto, TCreateDto, TUpdateDto> {

    /**
     * Converts a TCreateDto to an Entity.
     *
     * @param dto the TCreateDto to convert
     * @return the corresponding Entity
     */
    TModel toEntityFromCreateDto(TCreateDto dto);

    /**
     * Converts an TUpdateDto to an Entity.
     *
     * @param dto the TUpdateDto to convert
     * @return the corresponding Entity
     */
    TModel toEntityFromUpdateDto(TUpdateDto dto);

    /**
     * Converts an Entity to a TDto.
     *
     * @param entity the Entity to convert
     * @return the corresponding TDto
     */
    TDto toDto(TModel entity);
}
