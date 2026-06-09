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
public abstract class GenericCrudService<TEntity extends BaseEntity> implements CrudService<TEntity> {

    private final CrudRepository<TEntity> repository;

    protected GenericCrudService(CrudRepository<TEntity> repository) {
        this.repository = repository;
    }

    @Override
    public TEntity update(TEntity entity) {
        log.info("Updating entity: {}", entity);

        if (repository.findById(entity.getId()).isPresent()) {
            return repository.update(entity);
        }

        throw new NotFoundException("Entity " + entity + " not found");
    }

    @Override
    public Optional<TEntity> findById(Long id) {
        log.info("Finding entity by id: {}", id);

        return repository.findById(id);
    }

    @Override
    public Page<TEntity> findAll(Pageable pageable) {
        log.info("Finding all entities");

        return repository.findAll(pageable);
    }

    @Override
    public Optional<TEntity> softDeleteById(Long id) {
        log.info("Deleting entity by id: {}", id);

        return repository.softDeleteById(id);
    }

    @Override
    public void softDeleteAll(Pageable pageable) {
        log.info("Deleting all entities from pageable");

        repository.softDeleteAll(pageable);
    }
}
