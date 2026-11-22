package com.reservation.api.dto;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.Shift;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        Long customerId,
        String customerName,
        Long tableId,
        int tableNumber,
        int partySize,
        LocalDate date,
        Shift shift) {

    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getCustomer().getId(),
                r.getCustomer().getName(),
                r.getRestaurantTable().getId(),
                r.getRestaurantTable().getNumber(),
                r.getPartySize(),
                r.getDate(),
                r.getShift());
    }
}

