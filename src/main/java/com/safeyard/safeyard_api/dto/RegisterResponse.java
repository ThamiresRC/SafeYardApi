package com.safeyard.safeyard_api.dto;

/**
 * Resposta do cadastro /api/auth/register.
 * Retorna os dados principais do cliente criado.
 */
public record RegisterResponse(
        Long clienteId,
        String nome,
        String cpf,
        String email
) {}
