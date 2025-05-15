package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroDTO(
    Long id,
    @NotNull LocalDateTime dataHoraRegistro,
    @NotBlank String setor,
    @NotBlank String vaga,
    @NotBlank String tipo,
    @NotNull Long motoId
) {}
