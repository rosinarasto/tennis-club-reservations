package com.tennisclub.reservations.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenDto {

    private String accessToken;

    private String refreshToken;
}
