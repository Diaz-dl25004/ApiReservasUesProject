package com.reservation.exception;

public class TableCapacityExceededException extends RuntimeException {
    public TableCapacityExceededException(Long tableId, int partySize, int capacity) {
        super("La mesa " + tableId + " tiene capacidad para " + capacity
                + " personas y la reserva es para " + partySize);
    }
}

