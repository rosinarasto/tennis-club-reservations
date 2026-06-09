package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.User;

/**
 * Defines user-specific business operations.
 */
public interface UserService extends CrudService<User> {

    /**
     * Creates a new user.
     *
     * @param newUser user to create.
     * @return created user.
     */
    User create(User newUser);

    /**
     * Finds an existing user by phone number or creates a new one.
     *
     * @param user user data used for lookup or creation.
     * @return existing or newly created user.
     */
    User getOrCreate(User user);
}
