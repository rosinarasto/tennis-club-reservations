package com.tennisclub.reservations.service;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.User;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.model.factory.UserFactory;
import com.tennisclub.reservations.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void createUser_throwsException_phoneNumberExists() {
        var user = UserFactory.createUser("gg", "123456789");
        var userDTO = UserFactory.createCreateDto("gg", "123456789");

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ResourceAlreadyExistsException.class)
                .isThrownBy(() -> userService.create(userDTO))
                .withMessage("user with given phone number already exists");
    }

    @Test
    public void createUser() {
        var userDTO = UserFactory.createCreateDto("gg", "123456789");
        var user = UserFactory.createUser("gg", "123456789");
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        var actual = userService.create(userDTO);

        assertThat(actual).isNotNull();
        assertThat(actual.getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getName()).isEqualTo("gg");
        assertThat(actual.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    public void createUser_allowsDuplicateName() {
        var userDTO = UserFactory.createCreateDto("gg", "987654321");
        var user = UserFactory.createUser("gg", "987654321");
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        when(userRepository.findByPhoneNumber("987654321"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        var actual = userService.create(userDTO);

        assertThat(actual.getPhoneNumber()).isEqualTo("987654321");
        assertThat(actual.getName()).isEqualTo("gg");
    }

    @Test
    public void updateUser_throwsException() {
        var userDTO = UserFactory.createDto();
        userDTO.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userService.update(userDTO));
    }

    @Test
    public void findReservations_returnsEmpty() {
        when(userRepository.findByPhoneNumber("123456789"))
            .thenReturn(Optional.empty());

        assertThat(userService.findReservations("123456789", false).isEmpty()).isTrue();
    }

    @Test
    public void findNotFutureReservations_returnsOrderedReservations() {
        var user = UserFactory.createUser("123456789");

        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50))
        );

        user.setReservations(reservations);

        var reservationDTOs = List.of(
                ReservationFactory.createDto(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createDto(getTime(14, 0), getTime(15, 50))
        );

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.of(user));

        assertThat(userService.findReservations("123456789", false)).isEqualTo(reservationDTOs);
    }

    @Test
    public void findFutureReservations_returnsOrderedReservations() {
        var user = UserFactory.createUser("123456789");

        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50))
        );

        user.setReservations(reservations);

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.of(user));

        assertThat(userService.findReservations("123456789", true).isEmpty()).isTrue();
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
