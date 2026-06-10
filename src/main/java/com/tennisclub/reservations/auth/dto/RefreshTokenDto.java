package com.tennisclub.reservations.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a refresh-token request body.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {

    @NotBlank
    private String refreshToken;
}
