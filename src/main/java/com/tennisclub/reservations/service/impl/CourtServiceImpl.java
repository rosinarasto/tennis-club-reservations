package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.repository.CourtRepository;
import com.tennisclub.reservations.service.CourtService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class CourtServiceImpl extends GenericCrudService<Court> implements CourtService {

    private final CourtRepository courtRepository;

    @Autowired
    public CourtServiceImpl(CourtRepository repository) {
        super(repository);
        this.courtRepository = repository;
    }

    @Override
    public Court create(Court newCourt) {
        log.info("Creating new court: {}", newCourt);

        var court = courtRepository.findByCourtNumber(newCourt.getNumber());

        if (court.isPresent()) {
            throw new ResourceAlreadyExistsException("Court with number already exists");
        }

        return courtRepository.save(newCourt);
    }

    @Override
    public Court findByNumber(int number) {
        log.info("Finding court with number {}", number);

        return courtRepository.findByCourtNumber(number)
                .orElseThrow(() -> new NotFoundException("Court with number " + number + " not found"));
    }
}
