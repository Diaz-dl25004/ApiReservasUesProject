package com.reservation.infrastructure.persistence;

import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    boolean existsByRestaurantTable_IdAndDateAndShift(Long tableId, LocalDate date, Shift shift);

    boolean existsByRestaurantTable_IdAndDateAndShiftAndIdNot(Long tableId, LocalDate date, Shift shift, Long id);

    boolean existsByCustomer_Id(Long customerId);
}
