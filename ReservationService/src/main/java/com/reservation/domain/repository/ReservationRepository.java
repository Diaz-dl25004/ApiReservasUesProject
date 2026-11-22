package com.reservation.domain.repository;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.Shift;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

    List<Reservation> findAll();

    /** Valida disponibilidad; excludeId ignora la propia reserva al modificarla (puede ser null). */
    boolean existsByTableAndDateAndShift(Long tableId, LocalDate date, Shift shift, Long excludeId);

    /** Permite impedir borrar un cliente que tiene reservas. */
    boolean existsByCustomerId(Long customerId);

    void deleteById(Long id);
}
