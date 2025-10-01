package com.safeyard.safeyard_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String nome,

        @NotBlank
        @Size(min = 11, max = 14, message = "CPF deve ter 11 dígitos (com ou sem máscara).")
        @Pattern(
                regexp = "(\\d{11})|(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})",
                message = "CPF inválido"
        )
        String cpf,

        @NotBlank @Email String email,

        @NotBlank
        @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres.")
        String senha
) {}
