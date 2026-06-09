package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.model.factory.SurfaceFactory;
import com.tennisclub.reservations.service.SurfaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
public class SurfaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SurfaceService surfaceService;

    @Test
    public void createSurface_returnsCreatedSurface() throws Exception {
        var createDto = SurfaceFactory.createCreateDto("wet");
        var surfaceDto = SurfaceFactory.createDto(1L, "wet");

        when(surfaceService.create(createDto))
                .thenReturn(surfaceDto);

        mockMvc.perform(post("/api/surfaces")
                        .content(convertToJson(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("wet"));
    }

    @Test
    public void updateSurface_returnsUpdatedSurface() throws Exception {
        var surfaceDto = SurfaceFactory.createDto(1L, "wet");
        var updatedSurface = SurfaceFactory.createDto(1L, "clay");

        when(surfaceService.update(surfaceDto))
                .thenReturn(updatedSurface);

        mockMvc.perform(put("/api/surfaces")
                        .content(convertToJson(surfaceDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("clay"));
    }

    @Test
    public void updateSurface_throwsNotFoundException() throws Exception {
        var surfaceDto = SurfaceFactory.createDto(999L, "wet");

        when(surfaceService.update(surfaceDto))
                .thenThrow(new NotFoundException("Surface with id 999 not found"));

        mockMvc.perform(put("/api/surfaces")
                        .content(convertToJson(surfaceDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteSurface_returnsDeleted() throws Exception {
        var deleteDto = SurfaceFactory.createDto(1L, "wet");

        when(surfaceService.softDeleteById(1L))
                .thenReturn(Optional.of(deleteDto));

        mockMvc.perform(delete("/api/surfaces/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("wet"));
    }

    @Test
    public void deleteAllSurfaces_returnsOkResponse() throws Exception {
        mockMvc.perform(delete("/api/surfaces"))
                .andExpect(status().isOk());
    }

    @Test
    public void findSurfaceById_returnsSurface() throws Exception {
        var surface  = SurfaceFactory.createDto(1L, "wet");

        when(surfaceService.findById(1L))
                .thenReturn(Optional.of(surface));

        mockMvc.perform(get("/api/surfaces/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("wet"));
    }

    @Test
    public void findSurfaceById_returnsBadRequest() throws Exception {
        when(surfaceService.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/surfaces/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllSurfaces_returnsPaginatedSurfaces() throws Exception {
        var surfaces = List.of(
                SurfaceFactory.createDto(1L, "wet"),
                SurfaceFactory.createDto(2L, "dry")
        );

        when(surfaceService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(surfaces));

        mockMvc.perform(get("/api/surfaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("wet"))
                .andExpect(jsonPath("$.content[1].name").value("dry"));
    }
}
