package com.tennisclub.reservations.util;

import com.tennisclub.reservations.model.dto.ReservationDto;
import com.tennisclub.reservations.model.GameType;

import java.math.BigDecimal;
import java.time.Duration;

public class PriceCalculationUtil {

    public static BigDecimal calculatePrice(ReservationDto reservation) {
        var surfacePrice = reservation.getCourt().getSurface().getMinutePrice();

        var duration = Duration.between(reservation.getFrom(), reservation.getTo());
        var durationMinutes = BigDecimal.valueOf(duration.getSeconds() / 60);

        var multiplier = BigDecimal.valueOf(getMultiplier(reservation.getGameType()));

        return surfacePrice
                .multiply(durationMinutes)
                .multiply(multiplier);
    }

    private static double getMultiplier(GameType gameType) {
        return switch (gameType) {
            case SINGLES -> 1.0;
            case DOUBLES -> 1.5;
            default -> throw new IllegalArgumentException("unknown game type " + gameType);
        };
    }
}
