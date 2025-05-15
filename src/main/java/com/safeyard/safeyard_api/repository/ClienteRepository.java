package com.safeyard.safeyard_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safeyard.safeyard_api.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
