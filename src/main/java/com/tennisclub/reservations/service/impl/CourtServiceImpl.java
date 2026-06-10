package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.dto.create.CourtCreateDto;
import com.tennisclub.reservations.model.dto.update.CourtUpdateDto;
import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.repository.CourtRepository;
import com.tennisclub.reservations.service.CourtService;
import com.tennisclub.reservations.service.SurfaceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class CourtServiceImpl extends GenericCrudService<Court> implements CourtService {

    private final CourtRepository courtRepository;
    private final SurfaceService surfaceService;

    @Autowired
    public CourtServiceImpl(CourtRepository repository, SurfaceService surfaceService) {
        super(repository);
        this.courtRepository = repository;
        this.surfaceService = surfaceService;
    }

    @Override
    public Court create(CourtCreateDto createDto) {
        log.info("Creating new court: {}", createDto);

        var court = courtRepository.findByCourtNumber(createDto.getNumber());

        if (court.isPresent()) {
            throw new ResourceAlreadyExistsException("Court with number already exists");
        }

        var surfaceId = createDto.getSurfaceId();
        var surface = surfaceService.findById(surfaceId)
                .orElseThrow(() -> new NotFoundException("Surface with id " + surfaceId + " not found"));

        var newCourt = new Court(createDto.getName(), createDto.getNumber(), null, surface);
        return courtRepository.save(newCourt);
    }

    @Override
    public Court update(CourtUpdateDto updateDto) {
        log.info("Updating court: {}", updateDto);

        var surfaceId = updateDto.getSurfaceId();
        var surface = surfaceService.findById(surfaceId)
                .orElseThrow(() -> new NotFoundException("Surface with id " + surfaceId + " not found"));

        var court = new Court(updateDto.getName(), updateDto.getNumber(), null, surface);
        court.setId(updateDto.getId());

        return super.update(court);
    }

    @Override
    public Court findByNumber(int number) {
        log.info("Finding court with number {}", number);

        return courtRepository.findByCourtNumber(number)
                .orElseThrow(() -> new NotFoundException("Court with number " + number + " not found"));
    }
}
