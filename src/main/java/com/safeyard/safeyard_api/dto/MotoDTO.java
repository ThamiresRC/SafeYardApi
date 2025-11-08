package com.safeyard.safeyard_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados da moto")
public record MotoDTO(

        @Schema(description = "ID da moto", example = "1")
        Long id,

        @Schema(description = "Placa", example = "ABC1D23")
        @NotBlank(message = "A placa ÃƒÂ© obrigatÃƒÂ³ria")
        @Pattern(
                regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
                message = "Placa invÃƒÂ¡lida (padrÃƒÂ£o Mercosul: AAA1B23)"
        )
        String placa,

        @Schema(description = "Modelo", example = "CG 160")
        @NotBlank(message = "O modelo ÃƒÂ© obrigatÃƒÂ³rio")
        String modelo,

        @Schema(description = "Chassi", example = "9BWZZZ377VT000999")
        @NotBlank(message = "O chassi ÃƒÂ© obrigatÃƒÂ³rio")
        @Pattern(
                regexp = "^[A-HJ-NPR-Z0-9]{17}$",
                message = "Chassi invÃƒÂ¡lido (17 caracteres, sem I, O, Q)"
        )
        String chassi,

        @Schema(description = "Status", example = "DISPONIVEL")
        @NotBlank(message = "O status ÃƒÂ© obrigatÃƒÂ³rio")
        String status,

        @Schema(
                description = "URL pÃƒÂºblica da foto (preenchida apÃƒÂ³s upload)",
                example = "/files/placa_ABC1D23_1758330098463.jpg"
        )
        String fotoUrl
) {
        public static MotoDTO vazio() {
                return new MotoDTO(
                        null,
                        "",
                        "",
                        "",
                        "DISPONIVEL",
                        null
                );
        }
}
