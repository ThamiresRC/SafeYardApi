package com.safeyard.safeyard_api.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.service.MotoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/motos")
@RequiredArgsConstructor
public class MotoController {

    private final MotoService service;

    @PostMapping
    public MotoDTO create(@RequestBody @Valid MotoDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public MotoDTO update(@PathVariable Long id, @RequestBody @Valid MotoDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping
    public List<MotoDTO> findAll(Pageable pageable) {
        return service.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public MotoDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadImagem(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        String caminho = service.salvarImagem(id, file);
        return ResponseEntity.ok("Imagem salva com sucesso: " + caminho);
    }
}
