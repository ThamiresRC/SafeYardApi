package com.safeyard.safeyard_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "CLIENTE")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome obrigatorio")
    @Column(length = 100)
    private String nome;

    @NotBlank(message = "O CPF obrigatorio")
    @Column(length = 11, unique = true)
    private String cpf;

    @NotBlank(message = "O e-mail obrigatorio")
    @Email(message = "E-mail invalido")
    @Column(length = 100)
    private String email;
}
