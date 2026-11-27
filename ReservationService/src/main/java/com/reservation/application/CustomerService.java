package com.reservation.application;

import com.reservation.application.dto.CustomerCommand;
import com.reservation.exception.CustomerAlreadyExistsException;
import com.reservation.exception.CustomerHasReservationsException;
import com.reservation.exception.CustomerNotFoundException;
import com.reservation.domain.model.Customer;
import com.reservation.domain.repository.CustomerRepository;
import com.reservation.domain.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customers;
    private final ReservationRepository reservations;

    public CustomerService(CustomerRepository customers, ReservationRepository reservations) {
        this.customers = customers;
        this.reservations = reservations;
    }

    @Transactional
    public Customer create(CustomerCommand c) {
        String email = normalize(c.email());
        if (customers.existsByEmail(email, null)) {
            throw new CustomerAlreadyExistsException(email);
        }
        return customers.save(new Customer(c.name(), email, c.phone()));
    }

    @Transactional
    public Customer update(Long id, CustomerCommand c) {
        Customer customer = get(id);
        String email = normalize(c.email());
        if (customers.existsByEmail(email, id)) {
            throw new CustomerAlreadyExistsException(email);
        }
        customer.update(c.name(), email, c.phone());
        return customers.save(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = get(id);
        if (reservations.existsByCustomerId(id)) {
            throw new CustomerHasReservationsException(id);
        }
        customers.deleteById(customer.getId());
    }

    @Transactional(readOnly = true)
    public Customer get(Long id) {
        return customers.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Customer> list() {
        return customers.findAll();
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}

