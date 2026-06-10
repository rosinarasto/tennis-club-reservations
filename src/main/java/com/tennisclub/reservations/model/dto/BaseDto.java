package com.tennisclub.reservations.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base DTO for update and detail responses that expose an entity id.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDto {

    @NotNull
    private Long id;
}
