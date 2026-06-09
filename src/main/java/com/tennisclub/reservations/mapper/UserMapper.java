package com.tennisclub.reservations.mapper;

import com.tennisclub.reservations.model.dto.UserDto;
import com.tennisclub.reservations.model.dto.create.UserCreateDto;
import com.tennisclub.reservations.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, UserDto, UserDto> {

    User toEntityFromCreateDto(UserCreateDto dto);
}
