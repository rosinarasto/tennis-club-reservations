package com.tennisclub.reservations.service;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.entity.User;
import com.tennisclub.reservations.model.factory.UserFactory;
import com.tennisclub.reservations.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ResourceAlreadyExistsException.class)
                .isThrownBy(() -> userService.create(user))
                .withMessage("user with given phone number already exists");
    }

    @Test
    public void createUser() {
        var newUser = UserFactory.createUser("gg", "123456789");
        var user = UserFactory.createUser("gg", "123456789");
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        var actual = userService.create(newUser);

        assertThat(actual).isNotNull();
        assertThat(actual.getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getName()).isEqualTo("gg");
        assertThat(actual.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    public void createUser_allowsDuplicateName() {
        var newUser = UserFactory.createUser("gg", "987654321");
        var user = UserFactory.createUser("gg", "987654321");
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));

        when(userRepository.findByPhoneNumber("987654321"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        var actual = userService.create(newUser);

        assertThat(actual.getPhoneNumber()).isEqualTo("987654321");
        assertThat(actual.getName()).isEqualTo("gg");
    }

    @Test
    public void getOrCreate_returnsExistingUser() {
        var user = UserFactory.createUser("gg", "123456789");

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.of(user));

        assertThat(userService.getOrCreate(user)).isEqualTo(user);
    }

    @Test
    public void getOrCreate_createsNewUser() {
        var newUser = UserFactory.createUser("gg", "123456789");
        var user = UserFactory.createUser("gg", "123456789");

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.empty(), Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        assertThat(userService.getOrCreate(newUser)).isEqualTo(user);
    }

    @Test
    public void updateUser_throwsException() {
        var user = UserFactory.createUser();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userService.update(user));
    }

}
