package com.reservation.api;

import com.reservation.api.dto.TableResponse;
import com.reservation.domain.repository.TableRepository;
import com.reservation.exception.TableNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableRepository tables;

    public TableController(TableRepository tables) {
        this.tables = tables;
    }

    @GetMapping
    public List<TableResponse> list() {
        return tables.findAll().stream().map(TableResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TableResponse get(@PathVariable Long id) {
        return tables.findById(id)
                .map(TableResponse::from)
                .orElseThrow(() -> new TableNotFoundException(id));
    }
}
