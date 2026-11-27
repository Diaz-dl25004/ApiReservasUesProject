package com.reservation.api.dto;

import com.reservation.domain.model.Shift;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(
        @NotNull Long customerId,
        @NotNull Long tableId,
        @Min(1) int partySize,
        @NotNull @FutureOrPresent LocalDate date,
        @NotNull Shift shift) {
}