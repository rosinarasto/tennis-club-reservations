package com.tennisclub.reservations.model.factory;

import com.tennisclub.reservations.model.dto.CourtDto;
import com.tennisclub.reservations.model.dto.SurfaceDto;
import com.tennisclub.reservations.model.dto.create.CourtCreateDto;
import com.tennisclub.reservations.model.dto.update.CourtUpdateDto;
import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.entity.Reservation;

import java.util.List;

public class CourtFactory {

    private static final String DEFAULT_NAME = "court";

    public static Court createCourt(int number) {
        return new Court(DEFAULT_NAME, number, null, SurfaceFactory.createSurface());
    }

    public static Court createCourt(int number, List<Reservation> reservations) {
        return new Court(DEFAULT_NAME, number, reservations, SurfaceFactory.createSurface());
    }

    public static CourtDto createDto(Long id, int number) {
        var dto = new CourtDto(DEFAULT_NAME, number, SurfaceFactory.createDto());
        dto.setId(id);
        return dto;
    }

    public static CourtDto createDto(int number) {
        return new CourtDto(DEFAULT_NAME, number, SurfaceFactory.createDto());
    }

    public static CourtCreateDto createCreateDto(int number) {
        return new CourtCreateDto(DEFAULT_NAME, number, 1L);
    }

    public static CourtUpdateDto createUpdateDto(Long id, int number) {
        var dto = new CourtUpdateDto(DEFAULT_NAME, number, 1L);
        dto.setId(id);
        return dto;
    }
}
