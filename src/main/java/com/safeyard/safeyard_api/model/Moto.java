package com.safeyard.safeyard_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(
        name = "MOTO",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_moto_placa", columnNames = "PLACA"),
                @UniqueConstraint(name = "uk_moto_chassi", columnNames = "CHASSI")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A placa obrigatoria")
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
            message = "Placa invalida (padrao Mercosul: AAA1B23)")
    @Column(name = "PLACA", length = 7, nullable = false)
    private String placa;

    @NotBlank(message = "O modelo obrigatorio")
    @Column(name = "MODELO", length = 30, nullable = false)
    private String modelo;

    @NotBlank(message = "O chassi obrigatorio")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$",
            message = "Chassi invalido (17 caracteres, sem I, O, Q)")
    @Column(name = "CHASSI", length = 17, nullable = false)
    private String chassi;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private StatusMoto status;

    @Column(name = "FOTO_URL", length = 255)
    private String fotoUrl;

    @PrePersist @PreUpdate
    private void normalize() {
        if (placa != null) placa = placa.trim().toUpperCase();
        if (chassi != null) chassi = chassi.trim().toUpperCase();
        if (modelo != null) modelo = modelo.trim();
    }

    public enum StatusMoto {
        DISPONIVEL, EM_USO, MANUTENCAO
    }
}
