package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.User;

import java.util.Optional;

/**
 * Provides persistence operations for users.
 */
public interface UserRepository extends CrudRepository<User> {

    /**
     * Finds a non-deleted user by phone number.
     *
     * @param phoneNumber user phone number.
     * @return optional with found user, or empty optional if no user with given phone number is found.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
}
