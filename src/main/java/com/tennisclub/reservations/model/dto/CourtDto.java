package com.tennisclub.reservations.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourtDto extends BaseDto {

    @NotNull
    private String name;

    @NotNull
    private Integer number;

    @NotNull
    private SurfaceDto surface;
}
