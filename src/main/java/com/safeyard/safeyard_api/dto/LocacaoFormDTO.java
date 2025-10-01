package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocacaoFormDTO(
        Long id,                                  // usado só para update/delete
        @NotBlank @Size(max = 14) String cpf,     // pode vir com máscara
        @NotBlank @Size(max = 120) String nome,   // exibido no app; no back usamos o do banco
        @NotBlank @Size(max = 8)  String placa,   // ABC-1234
        @NotNull LocalDateTime dataRetirada,      // ISO ex.: 2025-09-30T00:00:00
        @NotNull LocalDateTime dataDevolucao
) {}
