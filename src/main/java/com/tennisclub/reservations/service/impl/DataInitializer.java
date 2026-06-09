package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.entity.Surface;
import com.tennisclub.reservations.repository.CourtRepository;
import com.tennisclub.reservations.repository.SurfaceRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DataInitializer {

    private final CourtRepository courtRepository;
    private final SurfaceRepository surfaceRepository;

    private static final PageRequest INIT_PAGE = PageRequest.of(0, Integer.MAX_VALUE);

    private static final List<SurfaceSeed> SURFACES = List.of(
            new SurfaceSeed("Hard", BigDecimal.valueOf(0.24)),
            new SurfaceSeed("Clay", BigDecimal.valueOf(0.36))
    );

    private static final List<CourtSeed> COURTS = List.of(
            new CourtSeed("Emerald Bay Tennis Center", 1, "Hard"),
            new CourtSeed("Riverside Racket Club", 2, "Clay"),
            new CourtSeed("Sunset Point Tennis Courts", 3, "Clay"),
            new CourtSeed("Grandview Athletic Park", 4, "Hard")
    );

    @Value("${data.init.enabled:false}")
    private boolean dataInitEnabled;

    @Autowired
    public DataInitializer(
            CourtRepository courtRepository,
            SurfaceRepository surfaceRepository
    ) {
        this.courtRepository = courtRepository;
        this.surfaceRepository = surfaceRepository;
    }

    @PostConstruct
    public void initData() {
        if (!dataInitEnabled)
            return;

        log.info("Data initialization started");

        var surfacesByName = initSurfaces();
        initCourts(surfacesByName);

        log.info("Data initialization finished");
    }

    private HashMap<String, Surface> initSurfaces() {
        var surfacesByName = new HashMap<String, Surface>();

        surfaceRepository.findAll(INIT_PAGE)
                .forEach(surface -> surfacesByName.put(surface.getName(), surface));

        SURFACES.stream()
                .filter(seed -> !surfacesByName.containsKey(seed.name()))
                .map(seed -> surfaceRepository.save(new Surface(seed.minutePrice(), seed.name(), new ArrayList<>())))
                .forEach(surface -> surfacesByName.put(surface.getName(), surface));

        return surfacesByName;
    }

    private void initCourts(HashMap<String, Surface> surfacesByName) {
        var existingCourtNumbers = courtRepository.findAll(INIT_PAGE).stream()
                .map(Court::getNumber)
                .collect(Collectors.toSet());

        COURTS.stream()
                .filter(seed -> !existingCourtNumbers.contains(seed.number()))
                .map(seed -> new Court(seed.name(), seed.number(), new ArrayList<>(), surfacesByName.get(seed.surfaceName())))
                .forEach(courtRepository::save);
    }

    private record SurfaceSeed(String name, BigDecimal minutePrice) {
    }

    private record CourtSeed(String name, int number, String surfaceName) {
    }
}
