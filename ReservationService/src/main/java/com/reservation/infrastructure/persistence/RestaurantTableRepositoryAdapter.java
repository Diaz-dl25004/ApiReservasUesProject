package com.reservation.infrastructure.persistence;

import com.reservation.domain.model.RestaurantTable;
import com.reservation.domain.repository.TableRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RestaurantTableRepositoryAdapter implements TableRepository {

    private final RestaurantTableJpaRepository jpa;

    public RestaurantTableRepositoryAdapter(RestaurantTableJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public RestaurantTable save(RestaurantTable table) {
        return jpa.save(table);
    }

    @Override
    public Optional<RestaurantTable> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<RestaurantTable> findAll() {
        return jpa.findAll();
    }

    @Override
    public boolean existsByNumber(int number, Long excludeId) {
        return excludeId == null
                ? jpa.existsByNumber(number)
                : jpa.existsByNumberAndIdNot(number, excludeId);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
