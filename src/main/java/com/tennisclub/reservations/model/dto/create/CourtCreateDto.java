package com.tennisclub.reservations.model.dto.create;

import com.tennisclub.reservations.model.dto.SurfaceDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourtCreateDto {

    @NotNull
    private String name;

    @NotNull
    private Integer number;

    @NotNull
    private SurfaceDto surface;
}
