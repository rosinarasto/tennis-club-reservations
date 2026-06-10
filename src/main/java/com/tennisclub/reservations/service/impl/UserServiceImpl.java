package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.model.entity.User;
import com.tennisclub.reservations.repository.UserRepository;
import com.tennisclub.reservations.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final String defaultUserPassword;

    @Autowired
    public UserServiceImpl(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${security.default-user-password}") String defaultUserPassword
    ) {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.defaultUserPassword = defaultUserPassword;
    }

    @Override
    public User create(User newUser) {
        log.info("Creating new User: {}", newUser);

        var userByPhoneNumber = userRepository.findByPhoneNumber(newUser.getPhoneNumber());

        if (userByPhoneNumber.isPresent()) {
            throw new ResourceAlreadyExistsException("user with given phone number already exists");
        }

        newUser.setRole(Role.USER);
        newUser.setPassword(passwordEncoder.encode(defaultUserPassword));

        return userRepository.save(newUser);
    }

    @Override
    public User getOrCreate(User user) {
        log.info("Finding or creating user with phone number {}", user.getPhoneNumber());

        return userRepository.findByPhoneNumber(user.getPhoneNumber())
                .orElseGet(() -> create(user));
    }

}
