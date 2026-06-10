package com.tennisclub.reservations.auth.dto;

import com.tennisclub.reservations.validator.annotation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents login credentials.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    @PhoneNumber
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}
