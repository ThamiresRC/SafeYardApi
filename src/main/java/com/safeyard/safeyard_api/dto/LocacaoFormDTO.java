package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocacaoFormDTO(
        Long id,
        @NotBlank @Size(max = 14) String cpf,
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 8)  String placa,
        @NotNull LocalDateTime dataRetirada,
        @NotNull LocalDateTime dataDevolucao
) {}
