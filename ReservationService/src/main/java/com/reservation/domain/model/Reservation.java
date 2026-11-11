package com.reservation.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Una reserva une a un cliente con una mesa en una fecha y turno.
 * Cliente 1 --- N Reservas N --- 1 Mesa
 */
@Entity
@Table(name = "reservations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reservation_table_date_shift",
                columnNames = {"table_id", "reservation_date", "shift"}))

public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable restaurantTable;

    @Column(name = "party_size", nullable = false)
    private int partySize;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Shift shift;

    protected Reservation() {
    }

    public Reservation(Customer customer, RestaurantTable restaurantTable,
                       int partySize, LocalDate date, Shift shift) {
        this.customer = customer;
        this.restaurantTable = restaurantTable;
        this.partySize = partySize;
        this.date = date;
        this.shift = shift;
    }

    public void update(Customer customer, RestaurantTable restaurantTable,
                       int partySize, LocalDate date, Shift shift) {
        this.customer = customer;
        this.restaurantTable = restaurantTable;
        this.partySize = partySize;
        this.date = date;
        this.shift = shift;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public RestaurantTable getRestaurantTable() { return restaurantTable; }
    public int getPartySize() { return partySize; }
    public LocalDate getDate() { return date; }
    public Shift getShift() { return shift; }
}
