package com.reservation.exception;

import com.reservation.domain.model.Shift;

import java.time.LocalDate;

public class TableNotAvailableException extends RuntimeException {
    public TableNotAvailableException(Long tableId, LocalDate date, Shift shift) {
        super("La mesa " + tableId + " ya esta reservada el " + date + " en el turno " + shift);
    }
}