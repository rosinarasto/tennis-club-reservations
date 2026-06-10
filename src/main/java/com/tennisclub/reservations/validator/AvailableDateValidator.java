package com.tennisclub.reservations.validator;

import com.tennisclub.reservations.service.ReservationService;
import com.tennisclub.reservations.validator.annotation.AvailableDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Slf4j
public class AvailableDateValidator implements ConstraintValidator<AvailableDate, Object> {

    private String courtField;
    private String fromField;
    private String toField;

    private final ReservationService reservationService;

    @Autowired
    public AvailableDateValidator(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public void initialize(AvailableDate constraintAnnotation) {
        this.courtField = constraintAnnotation.courtField();
        this.fromField = constraintAnnotation.fromField();
        this.toField = constraintAnnotation.toField();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        log.info("Checking availability of chosen date range.");

        Long courtId;
        LocalDateTime from;
        LocalDateTime to;

        try {
            courtId = (Long) PropertyUtils.getProperty(value, courtField);
            from = (LocalDateTime) PropertyUtils.getProperty(value, fromField);
            to = (LocalDateTime) PropertyUtils.getProperty(value, toField);
        } catch (InvocationTargetException | ClassCastException | IllegalAccessException | NoSuchMethodException ignored) {
            return false;
        }

        if (courtId == null || from == null || to == null || !from.isBefore(to)) {
            return false;
        }

        return reservationService.isDateAvailable(courtId, from, to);
    }
}
