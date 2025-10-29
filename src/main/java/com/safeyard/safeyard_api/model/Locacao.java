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
@Table(name = "LOCACAO")
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime dataSaida;

    private LocalDateTime dataDevolucao;

    @Column(length = 255)
    private String condicaoEntrega;

    @Column(length = 255)
    private String condicaoDevolucao;

    @Column(length = 200)
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "moto_id")
    private Moto moto;
}
