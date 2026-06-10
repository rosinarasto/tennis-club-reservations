package com.tennisclub.reservations.service;

import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.model.Role;
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

@SpringBootTest(properties = {
        "data.init.enabled=false",
        "security.default-user-password=default-password"
})
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

        when(userRepository.findByPhoneNumber("123456789"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var actual = userService.create(newUser);

        assertThat(actual).isNotNull();
        assertThat(actual.getPhoneNumber()).isEqualTo("123456789");
        assertThat(actual.getName()).isEqualTo("gg");
        assertThat(actual.getRole()).isEqualTo(Role.USER);
        assertThat(passwordEncoder.matches("default-password", actual.getPassword())).isTrue();
    }

    @Test
    public void createUser_allowsDuplicateName() {
        var newUser = UserFactory.createUser("gg", "987654321");

        when(userRepository.findByPhoneNumber("987654321"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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

}
