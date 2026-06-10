package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.entity.Surface;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.model.factory.SurfaceFactory;
import com.tennisclub.reservations.security.JwtService;
import com.tennisclub.reservations.service.ReservationService;
import com.tennisclub.reservations.service.SurfaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.tennisclub.reservations.TestUtils.convertToJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "data.init.enabled=false",
        "security.jwt.secret=test-secret-test-secret-test-secret",
        "security.jwt.access-token-expiration=PT15M"
})
@AutoConfigureMockMvc
public class JwtSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private SurfaceService surfaceService;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    public void securedEndpoint_returnsUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/surfaces"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void securedEndpoint_returnsUnauthorizedWhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/surfaces")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void openApiDocs_returnOkWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    public void openApiDocs_markSecuredEndpointsWithBearerAuth() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/api/courts/{id}'].get.security[0].bearerAuth").isArray())
                .andExpect(jsonPath("$.paths['/api/auth/login'].post.security").doesNotExist());
    }

    @Test
    public void swaggerUi_autoAuthorizesAccessTokenFromLoginResponse() throws Exception {
        mockMvc.perform(get("/swagger-ui/swagger-initializer.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("responseInterceptor")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("url.pathname === '/api/auth/login'")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("window.ui.authActions.authorize")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("bearerAuth")));
    }

    @Test
    public void securedEndpoint_returnsOkWhenTokenIsValid() throws Exception {
        when(surfaceService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/surfaces")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(Role.USER.getValue())))
                .andExpect(status().isOk());
    }

    @Test
    public void securedEndpoint_returnsUnauthorizedWhenRefreshTokenIsUsed() throws Exception {
        mockMvc.perform(get("/api/surfaces")
                        .header(HttpHeaders.AUTHORIZATION, refreshBearerToken(Role.USER.getValue())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void adminEndpoint_returnsForbiddenWhenUserTokenIsUsed() throws Exception {
        var createDto = SurfaceFactory.createCreateDto("wet");

        mockMvc.perform(post("/api/surfaces")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(Role.USER.getValue()))
                        .content(convertToJson(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void adminEndpoint_returnsOkWhenAdminTokenIsUsed() throws Exception {
        var createDto = SurfaceFactory.createCreateDto("wet");
        var surface = SurfaceFactory.createSurface("wet");
        surface.setId(1L);

        when(surfaceService.create(any(Surface.class)))
                .thenReturn(surface);

        mockMvc.perform(post("/api/surfaces")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(Role.ADMIN.getValue()))
                        .content(convertToJson(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void userEndpoint_returnsOkWhenUserCreatesReservation() throws Exception {
        var createDto = ReservationFactory.createCreateDto();
        var reservation = ReservationFactory.createReservation(
                LocalDateTime.of(2026, 12, 31, 13, 30),
                LocalDateTime.of(2026, 12, 31, 15, 0)
        );

        when(reservationService.isDateAvailable(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);
        when(reservationService.create(any(Reservation.class)))
                .thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(Role.USER.getValue()))
                        .content(convertToJson(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private String bearerToken(String authority) {
        var userDetails = User.withUsername("+421900000000")
                .password("")
                .authorities(authority)
                .build();

        return "Bearer " + jwtService.createAccessToken(userDetails);
    }

    private String refreshBearerToken(String authority) {
        var userDetails = User.withUsername("+421900000000")
                .password("")
                .authorities(authority)
                .build();

        return "Bearer " + jwtService.createRefreshToken(userDetails);
    }
}
