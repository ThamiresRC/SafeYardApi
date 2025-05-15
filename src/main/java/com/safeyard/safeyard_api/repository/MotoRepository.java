package com.safeyard.safeyard_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safeyard.safeyard_api.model.Moto;

public interface MotoRepository extends JpaRepository<Moto, Long> {
    Optional<Moto> findByPlaca(String placa);
}
