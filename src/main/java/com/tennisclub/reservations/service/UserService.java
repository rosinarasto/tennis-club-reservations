package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.User;

public interface UserService extends CrudService<User> {

    User create(User newUser);

    User getOrCreate(User user);
}
