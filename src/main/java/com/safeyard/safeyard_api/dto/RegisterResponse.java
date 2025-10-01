package com.safeyard.safeyard_api.dto;

public record RegisterResponse(
        Long clienteId,
        String nome,
        String cpf,
        String email
) {}
