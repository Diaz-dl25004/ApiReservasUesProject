package com.reservation.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String email) {
        super("Ya existe un cliente con el email " + email);
    }
}

