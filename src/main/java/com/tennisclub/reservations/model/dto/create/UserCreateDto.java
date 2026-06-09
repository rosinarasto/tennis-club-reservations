package com.tennisclub.reservations.model.dto.create;

import com.tennisclub.reservations.validator.annotation.PhoneNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotNull
    private String name;

    @PhoneNumber
    @NotNull
    private String phoneNumber;
}
