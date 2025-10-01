package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;

public record LocacaoViewDTO(
        Long id,
        String cpf,
        String nome,
        String placa,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao
) {}
