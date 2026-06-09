package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User> {

    Optional<User> findByPhoneNumber(String phoneNumber);
}
