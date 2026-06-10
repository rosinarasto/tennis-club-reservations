package com.tennisclub.reservations.service;

import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.model.entity.Court;
import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.SurfaceFactory;
import com.tennisclub.reservations.repository.CourtRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "data.init.enabled=false")
public class CourtServiceTest {

    @MockitoBean
    private CourtRepository courtRepository;

    @MockitoBean
    private SurfaceService surfaceService;

    @Autowired
    private CourtService courtService;

    @Test
    public void createCourt_throwsException() {
        var court = CourtFactory.createCourt(4);
        var createDto = CourtFactory.createCreateDto(4);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThatExceptionOfType(ResourceAlreadyExistsException.class)
                .isThrownBy(() -> courtService.create(createDto))
                .withMessage("Court with number already exists");
    }

    @Test
    public void createCourt() {
        var court = CourtFactory.createCourt(4);
        var createDto = CourtFactory.createCreateDto(4);
        var surface = SurfaceFactory.createSurface(1L);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.empty());

        when(surfaceService.findById(1L))
                .thenReturn(Optional.of(surface));

        when(courtRepository.save(any(Court.class)))
            .thenReturn(court);

        assertThat(courtService.create(createDto).getNumber()).isEqualTo(4);
    }

    @Test
    public void updateCourt_usesSurfaceFromUpdateDto() {
        var existingCourt = CourtFactory.createCourt(4);
        existingCourt.setId(1L);
        var surface = SurfaceFactory.createSurface(1L);
        var updateDto = CourtFactory.createUpdateDto(1L, 5);
        var updatedCourt = CourtFactory.createCourt(5);
        updatedCourt.setId(1L);
        updatedCourt.setSurface(surface);

        when(surfaceService.findById(1L))
                .thenReturn(Optional.of(surface));

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(existingCourt));

        when(courtRepository.update(any(Court.class)))
                .thenReturn(updatedCourt);

        var actual = courtService.update(updateDto);

        assertThat(actual.getNumber()).isEqualTo(5);
        assertThat(actual.getSurface()).isEqualTo(surface);
    }

    @Test
    public void findByNumber_returnsCourt() {
        var court = CourtFactory.createCourt(4);

        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.of(court));

        assertThat(courtService.findByNumber(4)).isEqualTo(court);
    }

    @Test
    public void findByNumber_throwsExceptionWhenCourtDoesNotExist() {
        when(courtRepository.findByCourtNumber(4))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> courtService.findByNumber(4))
                .withMessage("Court with number 4 not found");
    }
}
