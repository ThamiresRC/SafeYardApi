package com.safeyard.safeyard_api.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safeyard.safeyard_api.dto.ClienteDTO;
import com.safeyard.safeyard_api.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;


    @PostMapping
    public ClienteDTO create(@RequestBody @Valid ClienteDTO dto) {
        return service.create(dto);
    }


    @PutMapping("/{id}")
    public ClienteDTO update(@PathVariable Long id, @RequestBody @Valid ClienteDTO dto) {
        return service.update(id, dto);
    }


    @GetMapping
    public List<ClienteDTO> findAll(Pageable pageable) {
        return service.findAll(pageable).getContent();
    }


    @GetMapping("/{id}")
    public ClienteDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
