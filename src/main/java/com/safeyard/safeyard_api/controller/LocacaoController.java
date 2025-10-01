package com.safeyard.safeyard_api.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.dto.LocacaoFormDTO;
import com.safeyard.safeyard_api.dto.LocacaoViewDTO;
import com.safeyard.safeyard_api.service.LocacaoFacadeService;
import com.safeyard.safeyard_api.service.LocacaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/locacoes")
@RequiredArgsConstructor
public class LocacaoController {

    private final LocacaoService service;
    private final LocacaoFacadeService facade; // resolve cpf/placa e converte DTOs

    // --- ENDPOINTS ORIGINAIS ---

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

    // --- ENDPOINTS "AMIGOS DO FRONT" ---

    /** POST /api/locacoes/form  */
    @PostMapping("/form")
    public LocacaoViewDTO createFromForm(@RequestBody @Valid LocacaoFormDTO form) {
        return facade.createFromForm(form);
    }

    /** GET /api/locacoes/form */
    @GetMapping("/form")
    public List<LocacaoViewDTO> listForForm() {
        return facade.listForForm();
    }

    /** PUT /api/locacoes/form/{id} */
    @PutMapping("/form/{id}")
    public LocacaoViewDTO updateFromForm(@PathVariable Long id,
                                         @RequestBody @Valid LocacaoFormDTO form) {
        return facade.updateFromForm(id, form);
    }

    /** DELETE /api/locacoes/form/{id} */
    @DeleteMapping("/form/{id}")
    public void deleteFromForm(@PathVariable Long id) {
        facade.deleteById(id);
    }
}
