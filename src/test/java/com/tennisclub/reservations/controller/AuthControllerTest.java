package com.tennisclub.reservations.controller;

import com.jayway.jsonpath.JsonPath;
import com.tennisclub.reservations.auth.dto.AuthRequestDto;
import com.tennisclub.reservations.auth.dto.RefreshTokenDto;
import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.model.factory.UserFactory;
import com.tennisclub.reservations.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.tennisclub.reservations.TestUtils.convertToJson;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "data.init.enabled=false",
        "security.jwt.secret=test-secret-test-secret-test-secret",
        "security.jwt.access-token-expiration=PT15M"
})
@AutoConfigureMockMvc
public class AuthControllerTest {

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
                        .content(convertToJson(new AuthRequestDto(PHONE_NUMBER, PASSWORD)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", startsWith("ey")))
                .andExpect(jsonPath("$.refreshToken", startsWith("ey")));
    }

    @Test
    public void login_returnsUnauthorizedWhenPasswordIsInvalid() throws Exception {
        mockUser();

        mockMvc.perform(post("/api/auth/login")
                        .content(convertToJson(new AuthRequestDto(PHONE_NUMBER, "wrong-password")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    public void login_returnsBadRequestWhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request body is missing or invalid"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    public void refresh_returnsNewJwtTokens() throws Exception {
        mockUser();

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .content(convertToJson(new AuthRequestDto(PHONE_NUMBER, PASSWORD)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.refreshToken");

        mockMvc.perform(post("/api/auth/refresh")
                        .content(convertToJson(new RefreshTokenDto(refreshToken)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", startsWith("ey")))
                .andExpect(jsonPath("$.refreshToken", startsWith("ey")));
    }

    @Test
    public void refresh_returnsBadRequestWhenRefreshTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .content(convertToJson(new RefreshTokenDto("")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/auth/refresh"));
    }

    @Test
    public void refresh_returnsUnauthorizedWhenRefreshTokenIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .content(convertToJson(new RefreshTokenDto("invalid-token")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid token"))
                .andExpect(jsonPath("$.path").value("/api/auth/refresh"));
    }

    @Test
    public void refresh_returnsUnauthorizedWhenUserNoLongerExists() throws Exception {
        mockUser();

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .content(convertToJson(new AuthRequestDto(PHONE_NUMBER, PASSWORD)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        when(userRepository.findByPhoneNumber(PHONE_NUMBER))
                .thenReturn(Optional.empty());

        String refreshToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.refreshToken");

        mockMvc.perform(post("/api/auth/refresh")
                        .content(convertToJson(new RefreshTokenDto(refreshToken)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication failed"))
                .andExpect(jsonPath("$.path").value("/api/auth/refresh"));
    }

    private void mockUser() {
        var user = UserFactory.createUser("Admin", PHONE_NUMBER);
        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(PASSWORD));

        when(userRepository.findByPhoneNumber(PHONE_NUMBER))
                .thenReturn(Optional.of(user));
    }
}
