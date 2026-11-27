package com.reservation.api;

import com.reservation.domain.model.RestaurantTable;
import com.reservation.domain.repository.TableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TableController.class)
class TableControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TableRepository tables;

    private RestaurantTable table() {
        RestaurantTable t = new RestaurantTable(1, 4);
        ReflectionTestUtils.setField(t, "id", 10L);
        return t;
    }

    @Test
    void list_devuelve200ConLasMesas() throws Exception {
        when(tables.findAll()).thenReturn(List.of(table()));

        mvc.perform(get("/api/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[0].capacity").value(4));
    }

    @Test
    void get_mesaExistente_devuelve200() throws Exception {
        when(tables.findById(10L)).thenReturn(Optional.of(table()));

        mvc.perform(get("/api/tables/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void get_mesaInexistente_devuelve404() throws Exception {
        when(tables.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/tables/99"))
                .andExpect(status().isNotFound());
    }
}
