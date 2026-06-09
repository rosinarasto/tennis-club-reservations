package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.entity.Reservation;
import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.service.CourtService;
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
public class CourtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourtService courtService;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    public void createCourt_returnsCreatedCourt() throws Exception {
        var createDto = CourtFactory.createCreateDto(4);
        var court = CourtFactory.createCourt(4);
        court.setId(1L);

        when(courtService.create(any(Court.class)))
                .thenReturn(court);

        mockMvc.perform(post("/api/courts")
                        .content(convertToJson(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(4));
    }

    @Test
    public void updateCourt_returnsUpdatedCourt() throws Exception {
        var courtDto = CourtFactory.createDto(1L, 4);
        var court = CourtFactory.createCourt(4);
        court.setId(1L);

        when(courtService.update(any(Court.class)))
                .thenReturn(court);

        mockMvc.perform(put("/api/courts")
                        .content(convertToJson(courtDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(4));
    }

    @Test
    public void deleteCourt_returnsDeletedCourt() throws Exception {
        var court = CourtFactory.createCourt(4);
        court.setId(1L);

        when(courtService.softDeleteById(1L))
                .thenReturn(Optional.of(court));

        mockMvc.perform(delete("/api/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(4));
    }

    @Test
    public void deleteAllCourts_returnsOkResponse() throws Exception {
        mockMvc.perform(delete("/api/courts"))
                .andExpect(status().isOk());
    }

    @Test
    public void findCourtById_returnsCourt() throws Exception {
        var court = CourtFactory.createCourt(4);
        court.setId(1L);

        when(courtService.findById(1L))
                .thenReturn(Optional.of(court));

        mockMvc.perform(get("/api/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(4));
    }

    @Test
    public void findCourtById_returnsBadRequest() throws Exception {
        when(courtService.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/courts/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllCourts_returnsPaginatedCourts() throws Exception {
        var courts = List.of(
                CourtFactory.createCourt(4),
                CourtFactory.createCourt(5)
        );
        courts.get(0).setId(1L);
        courts.get(1).setId(2L);

        when(courtService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(courts));

        mockMvc.perform(get("/api/courts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].number").value(4))
                .andExpect(jsonPath("$.content[1].number").value(5));
    }

    @Test
    public void courtReservationsByCourtNumber_returnsReservations() throws Exception {
        var reservations = List.of(
                ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50))
        );

        when(reservationService.findByCourtNumber(4))
                .thenReturn(reservations);

        mockMvc.perform(get("/api/courts/4/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
