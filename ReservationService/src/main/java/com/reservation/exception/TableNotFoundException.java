package com.reservation.exception;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(Long id) {
        super("No existe la mesa con id " + id);
    }
}
