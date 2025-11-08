package com.safeyard.safeyard_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
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
@Table(name = "REGISTRO_MOTO_PATIO")
public class RegistroMotoPatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data e hora do registro sÃƒÂ£o obrigatÃƒÂ³rias")
    private LocalDateTime dataHoraRegistro;

    @NotNull(message = "O setor ÃƒÂ© obrigatÃƒÂ³rio")
    @Column(length = 30)
    private String setor;

    @NotNull(message = "A vaga ÃƒÂ© obrigatÃƒÂ³ria")
    @Column(length = 3)
    private String vaga;

    @NotNull(message = "O tipo de registro ÃƒÂ© obrigatÃƒÂ³rio") 
    @Column(length = 10)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "moto_id")
    private Moto moto;
}
