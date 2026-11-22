package com.reservation.infrastructure.persistence;

import com.reservation.domain.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableJpaRepository extends JpaRepository<RestaurantTable, Long> {

    boolean existsByNumber(int number);

    boolean existsByNumberAndIdNot(int number, Long id);
}
