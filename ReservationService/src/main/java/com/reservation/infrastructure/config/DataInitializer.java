package com.reservation.infrastructure.config;

import com.reservation.domain.model.Customer;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.RestaurantTable;
import com.reservation.domain.model.Shift;
import com.reservation.domain.repository.CustomerRepository;
import com.reservation.domain.repository.ReservationRepository;
import com.reservation.domain.repository.TableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Carga datos de ejemplo al arrancar la aplicación.
 * Solo inserta si las tablas están vacías, para no duplicar registros
 * en la base de datos compartida.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public DataInitializer(CustomerRepository customerRepository,
                           TableRepository tableRepository,
                           ReservationRepository reservationRepository) {
        this.customerRepository = customerRepository;
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        if (!customerRepository.findAll().isEmpty()
                || !tableRepository.findAll().isEmpty()
                || !reservationRepository.findAll().isEmpty()) {
            return;
        }

        Customer c1 = customerRepository.save(new Customer("Ana Martínez", "ana.martinez@example.com", "7000-1111"));
        Customer c2 = customerRepository.save(new Customer("Carlos Rivera", "carlos.rivera@example.com", "7000-2222"));
        Customer c3 = customerRepository.save(new Customer("María López", "maria.lopez@example.com", "7000-3333"));
        Customer c4 = customerRepository.save(new Customer("José Hernández", "jose.hernandez@example.com", "7000-4444"));
        Customer c5 = customerRepository.save(new Customer("Lucía Gómez", "lucia.gomez@example.com", "7000-5555"));

        RestaurantTable t1 = tableRepository.save(new RestaurantTable(1, 2));
        RestaurantTable t2 = tableRepository.save(new RestaurantTable(2, 4));
        RestaurantTable t3 = tableRepository.save(new RestaurantTable(3, 4));
        RestaurantTable t4 = tableRepository.save(new RestaurantTable(4, 6));
        RestaurantTable t5 = tableRepository.save(new RestaurantTable(5, 8));

        LocalDate hoy = LocalDate.now();

        reservationRepository.save(new Reservation(c1, t1, 2, hoy.plusDays(1), Shift.LUNCH));
        reservationRepository.save(new Reservation(c2, t2, 4, hoy.plusDays(1), Shift.DINNER));
        reservationRepository.save(new Reservation(c3, t4, 5, hoy.plusDays(2), Shift.LUNCH));
        reservationRepository.save(new Reservation(c4, t5, 8, hoy.plusDays(2), Shift.DINNER));
        reservationRepository.save(new Reservation(c5, t3, 3, hoy.plusDays(3), Shift.LUNCH));
    }
}
