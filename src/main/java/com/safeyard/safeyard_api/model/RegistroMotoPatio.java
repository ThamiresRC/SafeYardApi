package com.safeyard.safeyard_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroMotoPatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data e hora do registro são obrigatórias")
    private LocalDateTime dataHoraRegistro;

    @NotNull(message = "O setor é obrigatório")
    @Column(length = 30)
    private String setor;

    @NotNull(message = "A vaga é obrigatória")
    @Column(length = 3)
    private String vaga;

    @NotNull(message = "O tipo de registro é obrigatório") // entrada ou saída
    @Column(length = 10)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "moto_id")
    private Moto moto;
}
