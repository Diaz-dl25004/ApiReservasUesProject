package com.reservation.api;

import com.reservation.application.CustomerService;
import com.reservation.application.dto.CustomerCommand;
import com.reservation.domain.model.Customer;
import com.reservation.exception.CustomerAlreadyExistsException;
import com.reservation.exception.CustomerHasReservationsException;
import com.reservation.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String VALID_BODY =
            "{\"name\":\"Ana\",\"email\":\"ana@example.com\",\"phone\":\"7000-1111\"}";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CustomerService service;

    private Customer customer() {
        Customer c = new Customer("Ana", "ana@example.com", "7000-1111");
        ReflectionTestUtils.setField(c, "id", 1L);
        return c;
    }

    @Test
    void list_devuelve200ConLosClientes() throws Exception {
        when(service.list()).thenReturn(List.of(customer()));

        mvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@example.com"));
    }

    @Test
    void get_clienteInexistente_devuelve404() throws Exception {
        when(service.get(99L)).thenThrow(new CustomerNotFoundException(99L));

        mvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_cuerpoValido_devuelve201() throws Exception {
        when(service.create(any(CustomerCommand.class))).thenReturn(customer());

        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_emailInvalido_devuelve400SinLlamarAlServicio() throws Exception {
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana\",\"email\":\"no-es-un-email\",\"phone\":\"7000\"}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any());
    }

    @Test
    void create_emailDuplicado_devuelve409() throws Exception {
        when(service.create(any(CustomerCommand.class)))
                .thenThrow(new CustomerAlreadyExistsException("ana@example.com"));

        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_cliente_devuelve204() throws Exception {
        mvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void delete_clienteConReservas_devuelve409() throws Exception {
        doThrow(new CustomerHasReservationsException(1L)).when(service).delete(1L);

        mvc.perform(delete("/api/customers/1"))
                .andExpect(status().isConflict());
    }
}
