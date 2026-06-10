package com.tennisclub.reservations.model.dto;

import java.math.BigDecimal;

/**
 * Represents a reservation creation response.
 *
 * @param reservationId created reservation id.
 * @param price calculated reservation price.
 */
public record ReservationCreatedResponseDto(Long reservationId, BigDecimal price) {
}
