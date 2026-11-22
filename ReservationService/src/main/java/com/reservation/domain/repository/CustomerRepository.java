package com.reservation.domain.repository;

import com.reservation.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    List<Customer> findAll();

    /** excludeId permite ignorar al propio cliente al modificarlo (puede ser null). */
    boolean existsByEmail(String email, Long excludeId);

    void deleteById(Long id);
}
