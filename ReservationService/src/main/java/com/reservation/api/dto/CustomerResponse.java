package com.reservation.api.dto;

import com.reservation.domain.model.Customer;

public record CustomerResponse(Long id, String name, String email, String phone) {

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getName(), c.getEmail(), c.getPhone());
    }
}

