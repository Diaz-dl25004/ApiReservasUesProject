package com.reservation.domain.repository;

import com.reservation.domain.model.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface TableRepository {

    RestaurantTable save(RestaurantTable table);

    Optional<RestaurantTable> findById(Long id);

    List<RestaurantTable> findAll();

    /** excludeId permite ignorar la propia mesa al modificarla (puede ser null). */
    boolean existsByNumber(int number, Long excludeId);

    void deleteById(Long id);
}
