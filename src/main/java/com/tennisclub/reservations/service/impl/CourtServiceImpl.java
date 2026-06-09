package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.dto.CourtDto;
import com.tennisclub.reservations.dto.ReservationDto;
import com.tennisclub.reservations.dto.create.CourtCreateDto;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.mapper.CourtMapper;
import com.tennisclub.reservations.mapper.ReservationMapper;
import com.tennisclub.reservations.model.BaseEntity;
import com.tennisclub.reservations.model.Court;
import com.tennisclub.reservations.repository.CourtRepository;
import com.tennisclub.reservations.service.CourtService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional
public class CourtServiceImpl extends GenericCrudService<Court, CourtDto, CourtCreateDto, CourtDto> implements CourtService {

    private final CourtRepository courtRepository;

    private final ReservationMapper reservationMapper;
    private final CourtMapper courtMapper;

    @Autowired
    public CourtServiceImpl(CourtRepository repository, CourtMapper courtMapper, ReservationMapper reservationMapper) {
        super(repository, courtMapper, CourtDto.class, Court.class);
        this.courtRepository = repository;
        this.courtMapper = courtMapper;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public CourtDto create(CourtCreateDto newEntity) {
        log.info("Creating new court: {}", newEntity);

        var court = courtRepository.findByCourtNumber(newEntity.getNumber());

        if (court.isPresent()) {
            throw new ResourceAlreadyExistsException("Court with number already exists");
        }

        var entity = courtMapper.toEntityFromCreateDto(newEntity);
        var savedEntity = courtRepository.save(entity);
        return courtMapper.toDto(savedEntity);
    }

    @Override
    public List<ReservationDto> findReservations(int number) {
        log.info("Finding reservations of court: {}", number);

        var court = courtRepository.findByCourtNumber(number);

        return court.map(value ->
                        value.getReservations().stream()
                            .sorted(Comparator.comparing(
                                    BaseEntity::getCreationDate,
                                    Comparator.nullsLast(Comparator.naturalOrder())))
                            .map(reservationMapper::toDto)
                            .toList())
                    .orElse(Collections.emptyList());
    }
}
