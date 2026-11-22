package com.reservation.infrastructure.persistence;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.Shift;
import com.reservation.domain.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpa;

    public ReservationRepositoryAdapter(ReservationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return jpa.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Reservation> findAll() {
        return jpa.findAll();
    }

    @Override
    public boolean existsByTableAndDateAndShift(Long tableId, LocalDate date, Shift shift, Long excludeId) {
        return excludeId == null
                ? jpa.existsByRestaurantTable_IdAndDateAndShift(tableId, date, shift)
                : jpa.existsByRestaurantTable_IdAndDateAndShiftAndIdNot(tableId, date, shift, excludeId);
    }

    @Override
    public boolean existsByCustomerId(Long customerId) {
        return jpa.existsByCustomer_Id(customerId);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
