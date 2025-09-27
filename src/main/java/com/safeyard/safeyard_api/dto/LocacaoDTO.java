package com.safeyard.safeyard_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de locação.
 * - No POST do formulário, só vem (motoId, dataDevolucao, condicaoEntrega, condicaoDevolucao, qrCode opcional).
 * - O campo 'moto' é preenchido no service/controller para exibição (lista/detalhe).
 */
public record LocacaoDTO(
        Long id,

        // pode vir nulo do form; o controller garante um valor (now) se necessário
        LocalDateTime dataSaida,
        LocalDateTime dataDevolucao,

        @Size(max = 255) String condicaoEntrega,
        @Size(max = 255) String condicaoDevolucao,
        @Size(max = 200) String qrCode,

        // não vem do form por segurança; o controller define pelo usuário logado
        Long clienteId,

        // o usuário escolhe; mantemos obrigatório
        @NotNull Long motoId,

        // dados da moto para exibição nas telas (não é bindado no form)
        MotoDTO moto
) {}
