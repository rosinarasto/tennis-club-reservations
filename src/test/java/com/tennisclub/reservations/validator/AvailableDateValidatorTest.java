package com.tennisclub.reservations.validator;

import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.service.ReservationService;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AvailableDateValidatorTest {

    @MockitoBean
    private ReservationService reservationService;

    @Test
    public void isValid_givenAvailableReservation_returnsTrue() {
        var validator = new AvailableDateValidator(reservationService);
        var constraintAnnotation = new AnnotationDescriptor.Builder<>(AvailableDate.class).build().getAnnotation();
        validator.initialize(constraintAnnotation);

        var courtDto = CourtFactory.createDto(4);
        var reservationCreateDto = ReservationFactory.createCreateDto(courtDto, getTime(12, 0), getTime(13, 15));

        when(reservationService.isDateAvailable(courtDto.getNumber(), getTime(12, 0), getTime(13, 15)))
                .thenReturn(true);

        assertThat(validator.isValid(reservationCreateDto, null)).isTrue();
    }

    @Test
    public void isValid_givenUnavailableReservation_returnsFalse() {
        var validator = new AvailableDateValidator(reservationService);
        var constraintAnnotation = new AnnotationDescriptor.Builder<>(AvailableDate.class).build().getAnnotation();
        validator.initialize(constraintAnnotation);

        var courtDto = CourtFactory.createDto(4);
        var reservationCreateDto = ReservationFactory.createCreateDto(courtDto, getTime(12, 0), getTime(13, 15));

        when(reservationService.isDateAvailable(courtDto.getNumber(), getTime(12, 0), getTime(13, 15)))
                .thenReturn(false);

        assertThat(validator.isValid(reservationCreateDto, null)).isFalse();
    }

    @Test
    public void isValid_givenInvalidTimeRange_returnsFalse() {
        var validator = new AvailableDateValidator(reservationService);
        var constraintAnnotation = new AnnotationDescriptor.Builder<>(AvailableDate.class).build().getAnnotation();
        validator.initialize(constraintAnnotation);

        var courtDto = CourtFactory.createDto(4);
        var reservationCreateDto = ReservationFactory.createCreateDto(courtDto, getTime(13, 15), getTime(12, 0));

        assertThat(validator.isValid(reservationCreateDto, null)).isFalse();
        verify(reservationService, never()).isDateAvailable(courtDto.getNumber(), getTime(13, 15), getTime(12, 0));
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
