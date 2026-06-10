package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.model.dto.create.ReservationCreateDto;
import com.tennisclub.reservations.model.dto.update.ReservationUpdateDto;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.tennisclub.reservations.TestUtils.convertToJson;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = "ADMIN")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    public void createReservation_returnsPrice() throws Exception {
        var createDto = ReservationFactory.createCreateDto();
        var reservation = ReservationFactory.createReservation(getTime(13, 30), getTime(15, 0));

        when(reservationService.create(any(ReservationCreateDto.class)))
                .thenReturn(reservation);

        when(reservationService.isDateAvailable(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        var expected = BigDecimal.valueOf(126.0);

        mockMvc.perform(post("/api/reservations")
                        .content(convertToJson(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expected.toString()));
    }

    @Test
    public void updateReservation_returnsUpdatedReservation() throws Exception {
        var updateDto = ReservationFactory.createUpdateDto(1L);
        var reservation = ReservationFactory.createReservation(getTime(13, 30), getTime(15, 0));
        reservation.setId(1L);

        when(reservationService.update(any(ReservationUpdateDto.class)))
                .thenReturn(reservation);

        mockMvc.perform(put("/api/reservations")
                        .content(convertToJson(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void deleteReservation_returnsDeletedReservation() throws Exception {
        var reservation = ReservationFactory.createReservation(getTime(13, 30), getTime(15, 0));
        reservation.setId(1L);

        when(reservationService.softDeleteById(1L))
                .thenReturn(Optional.of(reservation));

        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void findReservationById_returnsReservation() throws Exception {
        var reservation = ReservationFactory.createReservation(getTime(13, 30), getTime(15, 0));
        reservation.setId(1L);

        when(reservationService.findById(1L))
                .thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void findReservationById_returnsBadRequest() throws Exception {
        when(reservationService.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Reservation with id 1 not found"))
                .andExpect(jsonPath("$.path").value("/api/reservations/1"));
    }

    @Test
    public void findAllReservations_returnsPaginatedReservations() throws Exception {
        var first = ReservationFactory.createReservation(getTime(13, 30), getTime(15, 0));
        first.setId(1L);
        var second = ReservationFactory.createReservation(getTime(15, 30), getTime(17, 0));
        second.setId(2L);

        when(reservationService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(first, second)));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.numberOfElements").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.pageable").doesNotExist())
                .andExpect(jsonPath("$.sort").doesNotExist());
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
