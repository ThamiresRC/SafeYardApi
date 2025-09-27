package com.safeyard.safeyard_api.repository;

import com.safeyard.safeyard_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
