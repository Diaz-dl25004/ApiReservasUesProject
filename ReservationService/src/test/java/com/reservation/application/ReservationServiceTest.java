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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservations;
    @Mock
    private CustomerRepository customers;
    @Mock
    private TableRepository tables;
    @InjectMocks
    private ReservationService service;

    private Customer customer;
    private RestaurantTable table;
    private final LocalDate date = LocalDate.of(2026, 12, 1);

    @BeforeEach
    void setUp() {
        customer = new Customer("Ana", "ana@example.com", "7000-1111");
        ReflectionTestUtils.setField(customer, "id", 1L);
        table = new RestaurantTable(1, 4);
        ReflectionTestUtils.setField(table, "id", 10L);
    }

    @Test
    void create_guardaLaReservaCuandoTodoEsValido() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(tables.findById(10L)).thenReturn(Optional.of(table));
        when(reservations.existsByTableAndDateAndShift(10L, date, Shift.LUNCH, null)).thenReturn(false);
        when(reservations.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation r = service.create(new CreateReservationCommand(1L, 10L, 4, date, Shift.LUNCH));

        assertEquals(4, r.getPartySize());
        assertEquals(Shift.LUNCH, r.getShift());
        verify(reservations).save(any(Reservation.class));
    }

    @Test
    void create_clienteInexistente_lanzaCustomerNotFound() {
        when(customers.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> service.create(new CreateReservationCommand(1L, 10L, 2, date, Shift.LUNCH)));
        verify(reservations, never()).save(any());
    }

    @Test
    void create_mesaInexistente_lanzaTableNotFound() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(tables.findById(10L)).thenReturn(Optional.empty());

        assertThrows(TableNotFoundException.class,
                () -> service.create(new CreateReservationCommand(1L, 10L, 2, date, Shift.LUNCH)));
        verify(reservations, never()).save(any());
    }

    @Test
    void create_capacidadExcedida_lanzaTableCapacityExceeded() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(tables.findById(10L)).thenReturn(Optional.of(table));

        assertThrows(TableCapacityExceededException.class,
                () -> service.create(new CreateReservationCommand(1L, 10L, 5, date, Shift.LUNCH)));
        verify(reservations, never()).save(any());
    }

    @Test
    void create_mesaOcupada_lanzaTableNotAvailable() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(tables.findById(10L)).thenReturn(Optional.of(table));
        when(reservations.existsByTableAndDateAndShift(10L, date, Shift.LUNCH, null)).thenReturn(true);

        assertThrows(TableNotAvailableException.class,
                () -> service.create(new CreateReservationCommand(1L, 10L, 2, date, Shift.LUNCH)));
        verify(reservations, never()).save(any());
    }

    @Test
    void update_ignoraLaPropiaReservaAlValidarDisponibilidad() {
        Reservation existing = new Reservation(customer, table, 2, date, Shift.DINNER);
        ReflectionTestUtils.setField(existing, "id", 5L);
        when(reservations.findById(5L)).thenReturn(Optional.of(existing));
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(tables.findById(10L)).thenReturn(Optional.of(table));
        when(reservations.existsByTableAndDateAndShift(10L, date, Shift.LUNCH, 5L)).thenReturn(false);
        when(reservations.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation r = service.update(5L, new UpdateReservationCommand(1L, 10L, 3, date, Shift.LUNCH));

        assertEquals(3, r.getPartySize());
        assertEquals(Shift.LUNCH, r.getShift());
        verify(reservations).existsByTableAndDateAndShift(10L, date, Shift.LUNCH, 5L);
    }

    @Test
    void delete_reservaInexistente_lanzaReservationNotFound() {
        when(reservations.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> service.delete(99L));
        verify(reservations, never()).deleteById(any());
    }
}
