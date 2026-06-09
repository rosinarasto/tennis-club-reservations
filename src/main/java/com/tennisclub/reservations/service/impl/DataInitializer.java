package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.model.dto.SurfaceDto;
import com.tennisclub.reservations.model.dto.create.CourtCreateDto;
import com.tennisclub.reservations.model.dto.create.SurfaceCreateDto;
import com.tennisclub.reservations.repository.CourtRepository;
import com.tennisclub.reservations.repository.SurfaceRepository;
import com.tennisclub.reservations.service.CourtService;
import com.tennisclub.reservations.service.SurfaceService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class DataInitializer {

    private final CourtService courtService;
    private final SurfaceService surfaceService;

    private final CourtRepository courtRepository;
    private final SurfaceRepository surfaceRepository;

    private final List<SurfaceDto> surfaces = new ArrayList<>();

    @Value("${data.init.enabled:false}")
    private boolean dataInitEnabled;

    @Autowired
    public DataInitializer(CourtService courtService, CourtRepository courtRepository,
                           SurfaceService surfaceService, SurfaceRepository surfaceRepository) {
        this.courtService = courtService;
        this.courtRepository = courtRepository;
        this.surfaceService = surfaceService;
        this.surfaceRepository = surfaceRepository;
    }

    @PostConstruct
    public void initData() {
        if (!dataInitEnabled)
            return;

        log.info("Data initialization started");

        initSurfaces();
        initCourts();

        log.info("Data initialization finished");
    }

    private void initSurfaces() {
        if (surfaceRepository.findByName("Hard").isEmpty())
            surfaces.add(surfaceService.create(new SurfaceCreateDto(BigDecimal.valueOf(0.24), "Hard")));
        if (surfaceRepository.findByName("Clay").isEmpty())
            surfaces.add(surfaceService.create(new SurfaceCreateDto(BigDecimal.valueOf(0.36), "Clay")));
    }

    private void initCourts() {
        if (courtRepository.findByCourtNumber(1).isEmpty())
            courtService.create(new CourtCreateDto("Emerald Bay Tennis Center", 1, surfaces.get(0)));
        if (courtRepository.findByCourtNumber(2).isEmpty())
            courtService.create(new CourtCreateDto("Riverside Racket Club", 2, surfaces.get(1)));
        if (courtRepository.findByCourtNumber(3).isEmpty())
            courtService.create(new CourtCreateDto("Sunset Point Tennis Courts", 3, surfaces.get(1)));
        if (courtRepository.findByCourtNumber(4).isEmpty())
            courtService.create(new CourtCreateDto("Grandview Athletic Park", 4, surfaces.get(0)));
    }
}
