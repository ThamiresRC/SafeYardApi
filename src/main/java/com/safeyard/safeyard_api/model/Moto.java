package com.safeyard.safeyard_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A placa é obrigatória")
    @Column(length = 7, unique = true, nullable = false)
    private String placa;

    @NotBlank(message = "O modelo é obrigatório")
    @Column(length = 30)
    private String modelo;

    @NotBlank(message = "O chassi é obrigatório")
    @Column(length = 17)
    private String chassi;

    @NotBlank(message = "O status é obrigatório")
    @Column(length = 30)
    private String status;
}
