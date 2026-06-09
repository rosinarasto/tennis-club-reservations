package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.tennisclub.reservations.TestUtils.convertToJson;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    public void createReservation_returnsPrice() throws Exception {
        var createDto = ReservationFactory.createCreateDto();
        var reservationDto = ReservationFactory.createDto();

        when(reservationService.create(createDto))
                .thenReturn(reservationDto);

        when(reservationService.isDateAvailable(reservationDto.getCourt().getNumber(), reservationDto.getFrom(), reservationDto.getTo()))
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
        var reservationDto = ReservationFactory.createDto();
        reservationDto.setId(1L);

        when(reservationService.update(reservationDto))
                .thenReturn(reservationDto);

        mockMvc.perform(put("/api/reservations")
                        .content(convertToJson(reservationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void deleteReservation_returnsDeletedReservation() throws Exception {
        var reservationDto = ReservationFactory.createDto();
        reservationDto.setId(1L);

        when(reservationService.softDeleteById(1L))
                .thenReturn(Optional.of(reservationDto));

        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void findReservationById_returnsReservation() throws Exception {
        var reservationDto = ReservationFactory.createDto();
        reservationDto.setId(1L);

        when(reservationService.findById(1L))
                .thenReturn(Optional.of(reservationDto));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void findReservationById_returnsBadRequest() throws Exception {
        when(reservationService.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllReservations_returnsPaginatedReservations() throws Exception {
        var first = ReservationFactory.createDto();
        first.setId(1L);
        var second = ReservationFactory.createDto();
        second.setId(2L);

        when(reservationService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(first, second)));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
