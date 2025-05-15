package com.safeyard.safeyard_api.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safeyard.safeyard_api.dto.RegistroDTO;
import com.safeyard.safeyard_api.service.RegistroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/registros")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroService service;

    @PostMapping
    public RegistroDTO create(@RequestBody @Valid RegistroDTO dto) {
        return service.create(dto);
    }

   @GetMapping("/filtro")
    public List<RegistroDTO> filtrar(
        @RequestParam(required = false) Long motoId,
        @RequestParam(required = false) String tipo,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
        Pageable pageable
) {
    return service.findByFilters(motoId, tipo, inicio, fim, pageable).getContent();
}

    
}
