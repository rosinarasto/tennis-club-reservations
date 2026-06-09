package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.mapper.GenericMapper;
import com.tennisclub.reservations.model.entity.BaseEntity;
import com.tennisclub.reservations.repository.CrudRepository;
import com.tennisclub.reservations.service.CrudService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Slf4j
@Transactional
public abstract class GenericCrudService<TModel extends BaseEntity, TDto, TCreateDto, TUpdateDto> implements CrudService<TDto, TCreateDto, TUpdateDto> {

    public final CrudRepository<TModel> repository;
    public final GenericMapper<TModel, TDto, TCreateDto, TUpdateDto> mapper;

    public final Class<TDto> dtoClass;
    public final Class<TModel> modelClass;

    public GenericCrudService(CrudRepository<TModel> repository,
                              GenericMapper<TModel, TDto, TCreateDto, TUpdateDto> mapper,
                              Class<TDto> dtoClass, Class<TModel> modelClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.dtoClass = dtoClass;
        this.modelClass = modelClass;
    }

    @Override
    public TDto create(TCreateDto newEntity) {
        log.info("Creating new entity: {}", newEntity);

        var entity = mapper.toEntityFromCreateDto(newEntity);
        var savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }

    @Override
    public TDto update(TUpdateDto updateEntity) {
        log.info("Updating entity: {}", updateEntity);

        var entity = mapper.toEntityFromUpdateDto(updateEntity);

        if (findById(entity.getId()).isPresent())
            return mapper.toDto(repository.update(entity));

        throw new NotFoundException("Entity " +updateEntity + " not found");
    }

    @Override
    public Optional<TDto> findById(Long id) {
        log.info("Finding entity by id: {}", id);

        var entity = repository.findById(id);

        return entity.map(mapper::toDto);
    }

    @Override
    public Page<TDto> findAll(Pageable pageable) {
        log.info("Finding all entities");

        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Optional<TDto> softDeleteById(Long id) {
        log.info("Deleting entity by id: {}", id);

        var entity = findById(id);

        if (entity.isEmpty())
            return Optional.empty();

        repository.softDeleteById(id);
        return entity;
    }

    @Override
    public void softDeleteAll(Pageable pageable) {
        log.info("Deleting all entities from pageable");

        repository.softDeleteAll(pageable);
    }
}
