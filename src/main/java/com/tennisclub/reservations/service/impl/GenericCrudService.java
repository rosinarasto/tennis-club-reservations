package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.NotFoundException;
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
public abstract class GenericCrudService<TModel extends BaseEntity> implements CrudService<TModel> {

    private final CrudRepository<TModel> repository;

    protected GenericCrudService(CrudRepository<TModel> repository) {
        this.repository = repository;
    }

    @Override
    public TModel update(TModel entity) {
        log.info("Updating entity: {}", entity);

        if (findById(entity.getId()).isPresent()) {
            return repository.update(entity);
        }

        throw new NotFoundException("Entity " + entity + " not found");
    }

    @Override
    public Optional<TModel> findById(Long id) {
        log.info("Finding entity by id: {}", id);

        return repository.findById(id);
    }

    @Override
    public Page<TModel> findAll(Pageable pageable) {
        log.info("Finding all entities");

        return repository.findAll(pageable);
    }

    @Override
    public Optional<TModel> softDeleteById(Long id) {
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
