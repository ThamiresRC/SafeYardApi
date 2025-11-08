package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.ClienteDTO;
import com.safeyard.safeyard_api.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public ResponseEntity<ClienteDTO> create(@RequestBody @Valid ClienteDTO dto) {
        ClienteDTO created = service.create(dto);
        // location: /api/clientes/{id}
        return ResponseEntity
                .created(URI.create("/api/clientes/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> update(@PathVariable Long id,
                                             @RequestBody @Valid ClienteDTO dto) {
        ClienteDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> findAll(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        Page<ClienteDTO> page = service.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> findById(@PathVariable Long id) {
        ClienteDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage() != null ? ex.getMessage() : "Registro não encontrado.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "Requisição inválida.");
    }
}
