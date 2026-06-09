package com.tennisclub.reservations.model.factory;

import com.tennisclub.reservations.model.dto.CourtDto;
import com.tennisclub.reservations.model.dto.SurfaceDto;
import com.tennisclub.reservations.model.dto.create.CourtCreateDto;
import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.entity.Surface;

import java.util.List;

public class CourtFactory {

    private static final String DEFAULT_NAME = "court";
    private static final Surface DEFAULT_SURFACE = SurfaceFactory.createSurface();

    private static final SurfaceDto DEFAULT_SURFACE_DTO = SurfaceFactory.createDto();

    public static Court createCourt(int number) {
        return new Court(DEFAULT_NAME, number, null, DEFAULT_SURFACE);
    }

    public static Court createCourt(int number, List<Reservation> reservations) {
        return new Court(DEFAULT_NAME, number, reservations, DEFAULT_SURFACE);
    }

    public static CourtDto createDto(Long id, int number) {
        var dto = new CourtDto(DEFAULT_NAME, number, DEFAULT_SURFACE_DTO);
        dto.setId(id);
        return dto;
    }

    public static CourtDto createDto(int number) {
        return new CourtDto(DEFAULT_NAME, number, DEFAULT_SURFACE_DTO);
    }

    public static CourtCreateDto createCreateDto(int number) {
        return new CourtCreateDto(DEFAULT_NAME, number, DEFAULT_SURFACE_DTO);
    }
}
