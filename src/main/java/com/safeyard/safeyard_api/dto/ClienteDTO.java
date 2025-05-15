package com.safeyard.safeyard_api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteDTO(
    Long id,
    @NotBlank String nome,
    @NotBlank String cpf,
    @NotBlank @Email String email
) {}
