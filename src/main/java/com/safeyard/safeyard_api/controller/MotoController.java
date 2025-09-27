package com.safeyard.safeyard_api.controller;

import java.net.URI;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.service.MotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/motos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Motos", description = "Endpoints para gerenciamento de motos")
public class MotoController {

    private final MotoService service;

    @Operation(summary = "Cria uma nova moto")
    @ApiResponse(responseCode = "201", description = "Criado")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MotoDTO> create(@RequestBody @Valid MotoDTO dto) {
        MotoDTO saved = service.create(dto);
        return ResponseEntity.created(URI.create("/api/motos/" + saved.id()))
                .body(saved);
    }

    @Operation(summary = "Atualiza uma moto existente")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MotoDTO> update(@PathVariable Long id, @RequestBody @Valid MotoDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Lista motos com paginação")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public Page<MotoDTO> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @Operation(summary = "Busca uma moto pelo ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/{id}")
    public MotoDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Exclui uma moto pelo ID")
    @ApiResponse(responseCode = "204", description = "Sem conteúdo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Upload da foto da moto",
            description = "Envia uma imagem (multipart/form-data) e salva a URL pública na moto",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Upload concluído",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Map.class)
                    )
            )
    )
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFoto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        String publicUrl = service.salvarImagem(id, file);
        String filename = publicUrl.substring(publicUrl.lastIndexOf('/') + 1);

        Map<String, String> body = Map.of(
                "message", "Upload realizado com sucesso",
                "filename", filename,
                "url", publicUrl
        );
        return ResponseEntity.ok(body);
    }
}
