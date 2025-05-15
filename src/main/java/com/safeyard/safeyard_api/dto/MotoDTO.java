package com.safeyard.safeyard_api.dto;

import jakarta.validation.constraints.NotBlank;

public record MotoDTO(
    Long id,
    @NotBlank String placa,
    @NotBlank String modelo,
    @NotBlank String chassi,
    @NotBlank String status
) {}
