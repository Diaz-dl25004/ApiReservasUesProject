package com.reservation.repository;

import com.reservation.domain.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    List<Customer> findAll();

    /** excludeId permite ignorar al propio cliente al modificarlo (puede ser null). */
    boolean existsByEmail(String email, Long excludeId);

    void deleteById(Long id);
}
