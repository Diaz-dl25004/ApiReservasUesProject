package com.reservation.application.dto;

import com.reservation.domain.model.Shift;
import java.time.LocalDate;

public record UpdateReservationCommand(Long customerId, Long tableId, int partySize, LocalDate date, Shift shift) {
}
