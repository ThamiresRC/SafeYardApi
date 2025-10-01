package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocacaoDTO(
        Long id,
        LocalDateTime dataSaida,
        LocalDateTime dataDevolucao,

        @Size(max = 255) String condicaoEntrega,
        @Size(max = 255) String condicaoDevolucao,
        @Size(max = 200) String qrCode,

        Long clienteId,

        @NotNull Long motoId,

        MotoDTO moto
) {}
