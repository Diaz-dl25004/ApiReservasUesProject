package com.reservation.exception;

public class CustomerHasReservationsException extends RuntimeException {
    public CustomerHasReservationsException(Long id) {
        super("No se puede eliminar al cliente " + id + " porque tiene reservas. Cancela sus reservas primero");
    }
}
