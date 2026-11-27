package com.reservation.application;

import com.reservation.application.dto.CreateReservationCommand;
import com.reservation.application.dto.UpdateReservationCommand;
import com.reservation.domain.model.Customer;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.RestaurantTable;
import com.reservation.domain.model.Shift;
import com.reservation.domain.repository.CustomerRepository;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.repository.TableRepository;
import com.reservation.exception.CustomerNotFoundException;
import com.reservation.exception.ReservationNotFoundException;
import com.reservation.exception.TableCapacityExceededException;
import com.reservation.exception.TableNotAvailableException;
import com.reservation.exception.TableNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservations;
    private final CustomerRepository customers;
    private final TableRepository tables;

    public ReservationService(ReservationRepository reservations,
                              CustomerRepository customers,
                              TableRepository tables) {
        this.reservations = reservations;
        this.customers = customers;
        this.tables = tables;
    }

    @Transactional
    public Reservation create(CreateReservationCommand c) {
        Customer customer = findCustomer(c.customerId());
        RestaurantTable table = findTable(c.tableId());
        validate(table, c.partySize(), c.date(), c.shift(), null);
        return reservations.save(
                new Reservation(customer, table, c.partySize(), c.date(), c.shift()));
    }

    @Transactional
    public Reservation update(Long id, UpdateReservationCommand c) {
        Reservation reservation = get(id);
        Customer customer = findCustomer(c.customerId());
        RestaurantTable table = findTable(c.tableId());
        validate(table, c.partySize(), c.date(), c.shift(), id);
        reservation.update(customer, table, c.partySize(), c.date(), c.shift());
        return reservations.save(reservation);
    }

    @Transactional
    public void delete(Long id) {
        Reservation reservation = get(id);
        reservations.deleteById(reservation.getId());
    }

    @Transactional(readOnly = true)
    public Reservation get(Long id) {
        return reservations.findById(id).orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Reservation> list() {
        return reservations.findAll();
    }

    /** Valida capacidad y disponibilidad. excludeId ignora la propia reserva al modificar (null al crear). */
    private void validate(RestaurantTable table, int partySize, LocalDate date, Shift shift, Long excludeId) {
        if (!table.canSeat(partySize)) {
            throw new TableCapacityExceededException(table.getId(), partySize, table.getCapacity());
        }
        if (reservations.existsByTableAndDateAndShift(table.getId(), date, shift, excludeId)) {
            throw new TableNotAvailableException(table.getId(), date, shift);
        }
    }

    private Customer findCustomer(Long id) {
        return customers.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    private RestaurantTable findTable(Long id) {
        return tables.findById(id).orElseThrow(() -> new TableNotFoundException(id));
    }
}
