package com.tennisclub.reservations.util;

import com.tennisclub.reservations.model.GameType;
import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.model.factory.SurfaceFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PriceCalculationUtilTest {

    private static final LocalDateTime DEFAULT_FROM = LocalDateTime.of(2024, 12, 31, 13, 30);
    private static final LocalDateTime DEFAULT_TO = LocalDateTime.of(2024, 12, 31, 15, 0);
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(1.2);

    @Test
    public void testCalculatePriceForSingles() {
        var reservation = createReservation(GameType.SINGLES);
        var price = PriceCalculationUtil.calculatePrice(reservation);
        assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(108));
    }

    @Test
    public void testCalculatePriceForDoubles() {
        var reservation = createReservation(GameType.DOUBLES);
        var price = PriceCalculationUtil.calculatePrice(reservation);
        assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(162));
    }

    private com.tennisclub.reservations.model.entity.Reservation createReservation(GameType gameType) {
        var surface = SurfaceFactory.createSurface();
        surface.setMinutePrice(DEFAULT_PRICE);

        var court = CourtFactory.createCourt(4);
        court.setSurface(surface);

        var reservation = ReservationFactory.createReservation(DEFAULT_FROM, DEFAULT_TO);
        reservation.setGameType(gameType);
        reservation.setCourt(court);

        return reservation;
    }
}
