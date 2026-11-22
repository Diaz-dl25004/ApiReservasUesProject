package com.reservation.infrastructure.persistence;

import com.reservation.domain.model.Customer;
import com.reservation.domain.repository.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpa;

    public CustomerRepositoryAdapter(CustomerJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Customer save(Customer customer) {
        return jpa.save(customer);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Customer> findAll() {
        return jpa.findAll();
    }

    @Override
    public boolean existsByEmail(String email, Long excludeId) {
        return excludeId == null
                ? jpa.existsByEmail(email)
                : jpa.existsByEmailAndIdNot(email, excludeId);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
