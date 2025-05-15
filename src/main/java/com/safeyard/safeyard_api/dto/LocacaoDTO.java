package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record LocacaoDTO(
    Long id,
    @NotNull LocalDateTime dataSaida,
    LocalDateTime dataDevolucao,
    String condicaoEntrega,
    String condicaoDevolucao,
    String qrCode,
    @NotNull Long clienteId,
    @NotNull Long motoId
) {}
