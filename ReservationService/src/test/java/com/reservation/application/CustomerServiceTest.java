package com.reservation.application;

import com.reservation.application.dto.CustomerCommand;
import com.reservation.domain.model.Customer;
import com.reservation.domain.repository.CustomerRepository;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.exception.CustomerAlreadyExistsException;
import com.reservation.exception.CustomerHasReservationsException;
import com.reservation.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customers;
    @Mock
    private ReservationRepository reservations;
    @InjectMocks
    private CustomerService service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("Ana", "ana@example.com", "7000-1111");
        ReflectionTestUtils.setField(customer, "id", 1L);
    }

    @Test
    void create_guardaElClienteConElEmailNormalizado() {
        when(customers.existsByEmail("ana@example.com", null)).thenReturn(false);
        when(customers.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer c = service.create(new CustomerCommand("Ana", "  ANA@Example.COM ", "7000-1111"));

        assertEquals("ana@example.com", c.getEmail());
        verify(customers).save(any(Customer.class));
    }

    @Test
    void create_emailDuplicado_lanzaCustomerAlreadyExists() {
        when(customers.existsByEmail("ana@example.com", null)).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class,
                () -> service.create(new CustomerCommand("Ana", "ana@example.com", "7000-1111")));
        verify(customers, never()).save(any());
    }

    @Test
    void update_cambiaLosDatosCuandoElEmailEsLibre() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(customers.existsByEmail("nuevo@example.com", 1L)).thenReturn(false);
        when(customers.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer c = service.update(1L, new CustomerCommand("Ana Maria", "Nuevo@Example.com", "7000-9999"));

        assertEquals("Ana Maria", c.getName());
        assertEquals("nuevo@example.com", c.getEmail());
    }

    @Test
    void update_emailDeOtroCliente_lanzaCustomerAlreadyExists() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(customers.existsByEmail("otro@example.com", 1L)).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class,
                () -> service.update(1L, new CustomerCommand("Ana", "otro@example.com", "7000-1111")));
        verify(customers, never()).save(any());
    }

    @Test
    void delete_clienteConReservas_lanzaCustomerHasReservations() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(reservations.existsByCustomerId(1L)).thenReturn(true);

        assertThrows(CustomerHasReservationsException.class, () -> service.delete(1L));
        verify(customers, never()).deleteById(any());
    }

    @Test
    void delete_clienteInexistente_lanzaCustomerNotFound() {
        when(customers.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.delete(99L));
        verify(customers, never()).deleteById(any());
    }

    @Test
    void delete_clienteSinReservas_loBorra() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(reservations.existsByCustomerId(1L)).thenReturn(false);

        service.delete(1L);

        verify(customers).deleteById(1L);
    }
}
