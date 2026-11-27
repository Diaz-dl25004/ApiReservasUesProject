package com.reservation.api;

import com.reservation.application.ReservationService;
import com.reservation.application.dto.CreateReservationCommand;
import com.reservation.domain.model.Customer;
import com.reservation.domain.model.Reservation;
import com.reservation.domain.model.RestaurantTable;
import com.reservation.domain.model.Shift;
import com.reservation.exception.ReservationNotFoundException;
import com.reservation.exception.TableCapacityExceededException;
import com.reservation.exception.TableNotAvailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    private static final LocalDate DATE = LocalDate.of(2030, 1, 15);
    private static final String VALID_BODY =
            "{\"customerId\":1,\"tableId\":10,\"partySize\":2,\"date\":\"2030-01-15\",\"shift\":\"LUNCH\"}";
    private static final String INVALID_BODY =
            "{\"customerId\":1,\"tableId\":10,\"partySize\":0,\"date\":\"2030-01-15\",\"shift\":\"LUNCH\"}";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ReservationService service;

    private Reservation reservation() {
        Customer c = new Customer("Ana", "ana@example.com", "7000-1111");
        ReflectionTestUtils.setField(c, "id", 1L);
        RestaurantTable t = new RestaurantTable(1, 4);
        ReflectionTestUtils.setField(t, "id", 10L);
        Reservation r = new Reservation(c, t, 2, DATE, Shift.LUNCH);
        ReflectionTestUtils.setField(r, "id", 5L);
        return r;
    }

    @Test
    void list_devuelve200ConLasReservas() throws Exception {
        when(service.list()).thenReturn(List.of(reservation()));

        mvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Ana"));
    }

    @Test
    void get_reservaInexistente_devuelve404() throws Exception {
        when(service.get(99L)).thenThrow(new ReservationNotFoundException(99L));

        mvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_cuerpoValido_devuelve201() throws Exception {
        when(service.create(any(CreateReservationCommand.class))).thenReturn(reservation());

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.tableNumber").value(1));
    }

    @Test
    void create_mesaOcupada_devuelve409() throws Exception {
        when(service.create(any(CreateReservationCommand.class)))
                .thenThrow(new TableNotAvailableException(10L, DATE, Shift.LUNCH));

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void create_capacidadExcedida_devuelve400() throws Exception {
        when(service.create(any(CreateReservationCommand.class)))
                .thenThrow(new TableCapacityExceededException(10L, 5, 4));

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_cuerpoInvalido_devuelve400SinLlamarAlServicio() throws Exception {
        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any());
    }

    @Test
    void delete_reserva_devuelve204() throws Exception {
        mvc.perform(delete("/api/reservations/5"))
                .andExpect(status().isNoContent());

        verify(service).delete(5L);
    }
}
