package com.tennisclub.reservations.model.factory;

import com.tennisclub.reservations.model.dto.SurfaceDto;
import com.tennisclub.reservations.model.dto.create.SurfaceCreateDto;
import com.tennisclub.reservations.model.entity.Surface;

import java.math.BigDecimal;

public class SurfaceFactory {

    private static final BigDecimal DEFAULT_MINUTE_PRICE = BigDecimal.valueOf(1.4);
    private static final String DEFAULT_NAME = "hard";

    public static Surface createSurface(String name) {
        return new Surface(DEFAULT_MINUTE_PRICE, name, null);
    }

    public static Surface createSurface() {
        return new Surface(DEFAULT_MINUTE_PRICE, DEFAULT_NAME, null);
    }

    public static SurfaceCreateDto createCreateDto(String name) {
        return new SurfaceCreateDto(DEFAULT_MINUTE_PRICE, name);
    }

    public static SurfaceDto createDto(Long id, String name) {
        var dto = new SurfaceDto(DEFAULT_MINUTE_PRICE, name);
        dto.setId(id);
        return dto;
    }

    public static SurfaceDto createDto() {
        return new SurfaceDto(DEFAULT_MINUTE_PRICE, DEFAULT_NAME);
    }
}
