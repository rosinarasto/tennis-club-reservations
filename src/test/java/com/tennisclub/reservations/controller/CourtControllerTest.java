package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.service.CourtService;
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

    @Test
    public void createCourt_returnsCreatedCourt() throws Exception {
        var createDto = CourtFactory.createCreateDto(4);
        var courtDto = CourtFactory.createDto(1L, 4);

        when(courtService.create(createDto))
                .thenReturn(courtDto);

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

        when(courtService.update(courtDto))
                .thenReturn(courtDto);

        mockMvc.perform(put("/api/courts")
                        .content(convertToJson(courtDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(4));
    }

    @Test
    public void deleteCourt_returnsDeletedCourt() throws Exception {
        var courtDto = CourtFactory.createDto(1L, 4);

        when(courtService.softDeleteById(1L))
                .thenReturn(Optional.of(courtDto));

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
        var courtDto = CourtFactory.createDto(1L, 4);

        when(courtService.findById(1L))
                .thenReturn(Optional.of(courtDto));

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
                CourtFactory.createDto(1L, 4),
                CourtFactory.createDto(2L, 5)
        );

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
        var reservationDTOs = List.of(
                ReservationFactory.createDto(getTime(12, 0), getTime(13, 30)),
                ReservationFactory.createDto(getTime(14, 0), getTime(15, 50))
        );

        when(courtService.findReservations(4))
                .thenReturn(reservationDTOs);

        mockMvc.perform(get("/api/courts/4/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
