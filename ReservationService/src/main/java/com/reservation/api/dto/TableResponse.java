package com.reservation.api.dto;

import com.reservation.domain.model.RestaurantTable;

public record TableResponse(Long id, int number, int capacity) {

    public static TableResponse from(RestaurantTable t) {
        return new TableResponse(t.getId(), t.getNumber(), t.getCapacity());
    }
}