package com.tennisclub.reservations.service.impl;

import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.dto.UserDto;
import com.tennisclub.reservations.model.dto.create.UserCreateDto;
import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.mapper.ReservationMapper;
import com.tennisclub.reservations.mapper.UserMapper;
import com.tennisclub.reservations.model.entity.User;
import com.tennisclub.reservations.repository.UserRepository;
import com.tennisclub.reservations.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional
public class UserServiceImpl extends GenericCrudService<User, UserDto, UserCreateDto, UserDto> implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper mapper,
                           ReservationMapper reservationMapper, PasswordEncoder passwordEncoder) {
        super(repository, mapper, UserDto.class, User.class);
        this.userRepository = repository;
        this.userMapper = mapper;
        this.reservationMapper = reservationMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto create(UserCreateDto newUser) {
        log.info("Creating new User: {}", newUser);

        var userByPhoneNumber = userRepository.findByPhoneNumber(newUser.getPhoneNumber());

        if (userByPhoneNumber.isPresent()) {
            throw new ResourceAlreadyExistsException("user with given phone number already exists");
        }

        var user = userMapper.toEntityFromCreateDto(newUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto updateUser) {
        log.info("Updating User: {}", updateUser);

        var user = userMapper.toEntityFromUpdateDto(updateUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (findById(user.getId()).isPresent())
            return userMapper.toDto(userRepository.update(user));

        throw new NotFoundException("User " + updateUser + "not found");
    }

    @Override
    public List<ReservationDto> findReservations(String phoneNumber, boolean future) {
        var user = userRepository.findByPhoneNumber(phoneNumber);

        return user.map(value ->
                        value.getReservations().stream()
                            .map(reservationMapper::toDto)
                                .filter(res -> !future || res.getFrom().isAfter(LocalDateTime.now()))
                                .toList())
                    .orElse(Collections.emptyList());
    }
}
