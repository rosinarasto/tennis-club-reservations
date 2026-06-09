package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.entity.User;
import com.tennisclub.reservations.repository.UserRepository;
import com.tennisclub.reservations.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class UserServiceImpl extends GenericCrudService<User> implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User newUser) {
        log.info("Creating new User: {}", newUser);

        var userByPhoneNumber = userRepository.findByPhoneNumber(newUser.getPhoneNumber());

        if (userByPhoneNumber.isPresent()) {
            throw new ResourceAlreadyExistsException("user with given phone number already exists");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userRepository.save(newUser);
    }

    @Override
    public User getOrCreate(User user) {
        log.info("Finding or creating user with phone number {}", user.getPhoneNumber());

        return userRepository.findByPhoneNumber(user.getPhoneNumber())
                .orElseGet(() -> create(user));
    }

    @Override
    public User update(User updateUser) {
        log.info("Updating User: {}", updateUser);

        updateUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));

        if (findById(updateUser.getId()).isPresent()) {
            return userRepository.update(updateUser);
        }

        throw new NotFoundException("User " + updateUser + "not found");
    }
}
