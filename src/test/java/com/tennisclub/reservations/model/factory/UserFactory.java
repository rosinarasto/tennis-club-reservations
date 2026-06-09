package com.tennisclub.reservations.model.factory;

import com.tennisclub.reservations.model.dto.UserDto;
import com.tennisclub.reservations.model.dto.create.UserCreateDto;
import com.tennisclub.reservations.model.entity.User;

import java.util.ArrayList;

public class UserFactory {

    private static final String DEFAULT_NAME = "jj";
    private static final String DEFAULT_PHONE_NUMBER = "+44 20 7946 0958";
    private static final String DEFAULT_PASSWORD = "password";

    public static User createUser() {
        return new User(DEFAULT_NAME, DEFAULT_PHONE_NUMBER, DEFAULT_PASSWORD, null);
    }

    public static User createUser(String name, String phoneNumber) {
        return new User(name, phoneNumber, DEFAULT_PASSWORD, new ArrayList<>());
    }

    public static UserDto createDto() {
        return new UserDto(DEFAULT_NAME, DEFAULT_PHONE_NUMBER, DEFAULT_PASSWORD);
    }

    public static UserCreateDto createCreateDto() {
        return new UserCreateDto(DEFAULT_NAME, DEFAULT_PHONE_NUMBER, DEFAULT_PASSWORD);
    }
}
