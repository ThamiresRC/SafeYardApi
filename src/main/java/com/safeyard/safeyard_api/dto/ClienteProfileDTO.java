package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;

public record ClienteProfileDTO(
        Long clienteId,
        String nome,
        String email,
        String cpf,
        Long locacaoId,
        String placa,
        LocalDateTime dataSaida,
        LocalDateTime dataDevolucao
) {}
