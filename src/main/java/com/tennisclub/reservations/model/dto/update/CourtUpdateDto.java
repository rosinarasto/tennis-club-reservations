package com.tennisclub.reservations.model.dto.update;

import com.tennisclub.reservations.model.dto.BaseDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a court.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourtUpdateDto extends BaseDto {

    @NotNull
    private String name;

    @NotNull
    private Integer number;

    @NotNull
    @Positive
    private Long surfaceId;
}
