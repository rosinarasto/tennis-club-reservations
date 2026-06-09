package com.tennisclub.reservations.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurfaceDto extends BaseDto {

    @NotNull
    private BigDecimal minutePrice;

    @NotNull
    private String name;
}
