package com.reservation.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true)
    private int number;

    @Column(nullable = false)
    private int capacity;

    protected RestaurantTable() {
    }

    public RestaurantTable(int number, int capacity) {
        this.number = number;
        this.capacity = capacity;
    }

    public boolean canSeat(int partySize) {
        return partySize <= capacity;
    }

    public Long getId() { return id; }
    public int getNumber() { return number; }
    public int getCapacity() { return capacity; }
}
