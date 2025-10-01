// src/main/java/com/safeyard/safeyard_api/dto/ClienteProfileDTO.java
package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;

public record ClienteProfileDTO(
        Long clienteId,
        String nome,
        String email,
        String cpf,
        // info da locação atual/mais recente (se houver)
        Long locacaoId,
        String placa,
        LocalDateTime dataSaida,
        LocalDateTime dataDevolucao
) {}
