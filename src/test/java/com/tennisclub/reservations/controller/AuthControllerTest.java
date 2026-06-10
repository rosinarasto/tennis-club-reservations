package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.model.factory.UserFactory;
import com.tennisclub.reservations.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "data.init.enabled=false",
        "security.jwt.secret=test-secret-test-secret-test-secret",
        "security.jwt.access-token-expiration=PT15M"
})
@AutoConfigureMockMvc
public class AuthControllerTest {

    private static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";
    private static final String PHONE_NUMBER = "+421900000000";
    private static final String PASSWORD = "admin";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void login_returnsJwtToken() throws Exception {
        mockUser();

        mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, basic(PHONE_NUMBER, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")))
                .andExpect(header().string(REFRESH_TOKEN_HEADER, startsWith("Bearer ")));
    }

    @Test
    public void login_returnsUnauthorizedWhenPasswordIsInvalid() throws Exception {
        mockUser();

        mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, basic(PHONE_NUMBER, "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_returnsUnauthorizedWhenBasicHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void refresh_returnsNewJwtTokens() throws Exception {
        mockUser();

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, basic(PHONE_NUMBER, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        var refreshToken = loginResult.getResponse().getHeader(REFRESH_TOKEN_HEADER);

        mockMvc.perform(post("/api/auth/refresh")
                        .header(HttpHeaders.AUTHORIZATION, refreshToken))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")))
                .andExpect(header().string(REFRESH_TOKEN_HEADER, startsWith("Bearer ")));
    }

    @Test
    public void refresh_returnsUnauthorizedWhenRefreshTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void refresh_returnsUnauthorizedWhenRefreshTokenIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void refresh_returnsUnauthorizedWhenUserNoLongerExists() throws Exception {
        mockUser();

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, basic(PHONE_NUMBER, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        when(userRepository.findByPhoneNumber(PHONE_NUMBER))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/refresh")
                        .header(HttpHeaders.AUTHORIZATION, loginResult.getResponse().getHeader(REFRESH_TOKEN_HEADER)))
                .andExpect(status().isUnauthorized());
    }

    private String basic(String username, String password) {
        var credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private void mockUser() {
        var user = UserFactory.createUser("Admin", PHONE_NUMBER);
        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(PASSWORD));

        when(userRepository.findByPhoneNumber(PHONE_NUMBER))
                .thenReturn(Optional.of(user));
    }
}
