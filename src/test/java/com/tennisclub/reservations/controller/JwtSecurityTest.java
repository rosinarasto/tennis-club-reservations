package com.tennisclub.reservations.controller;

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
    public void securedEndpoint_returnsOkWhenTokenIsValid() throws Exception {
        when(surfaceService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/surfaces")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("USER")))
                .andExpect(status().isOk());
    }

    @Test
    public void adminEndpoint_returnsForbiddenWhenUserTokenIsUsed() throws Exception {
        var createDto = SurfaceFactory.createCreateDto("wet");

        mockMvc.perform(post("/api/surfaces")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("USER"))
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
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("ADMIN"))
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
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("USER"))
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
}
