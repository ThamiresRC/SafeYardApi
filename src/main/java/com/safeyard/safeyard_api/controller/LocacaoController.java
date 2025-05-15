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

import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.service.LocacaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/locacoes")
@RequiredArgsConstructor
public class LocacaoController {

    private final LocacaoService service;

    @PostMapping
    public LocacaoDTO create(@RequestBody @Valid LocacaoDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/filtro")
    public List<LocacaoDTO> filtrar(
        @RequestParam(required = false) Long clienteId,
        @RequestParam(required = false) Long motoId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
        Pageable pageable
) {
    return service.findByFilters(clienteId, motoId, inicio, fim, pageable).getContent();
}

}
