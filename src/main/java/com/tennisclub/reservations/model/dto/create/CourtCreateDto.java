package com.tennisclub.reservations.model.dto.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a court.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourtCreateDto {

    @NotNull
    private String name;

    @NotNull
    private Integer number;

    @NotNull
    @Positive
    private Long surfaceId;
}
