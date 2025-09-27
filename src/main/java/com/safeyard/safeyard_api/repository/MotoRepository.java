package com.safeyard.safeyard_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.safeyard.safeyard_api.model.Moto;

public interface MotoRepository extends JpaRepository<Moto, Long> {
    boolean existsByPlacaIgnoreCaseAndIdNot(String placa, Long id);

    boolean existsByChassiIgnoreCaseAndIdNot(String chassi, Long id);
}