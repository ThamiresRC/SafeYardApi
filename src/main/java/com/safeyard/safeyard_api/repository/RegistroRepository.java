package com.safeyard.safeyard_api.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.safeyard.safeyard_api.model.RegistroMotoPatio;

public interface RegistroRepository extends JpaRepository<RegistroMotoPatio, Long> {

    @Query("""
        SELECT r FROM RegistroMotoPatio r
        WHERE (:motoId IS NULL OR r.moto.id = :motoId)
        AND (:tipo IS NULL OR r.tipo = :tipo)
        AND (:inicio IS NULL OR r.dataHoraRegistro >= :inicio)
        AND (:fim IS NULL OR r.dataHoraRegistro <= :fim)
    """)
    Page<RegistroMotoPatio> findByFilters(
        @Param("motoId") Long motoId,
        @Param("tipo") String tipo,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        Pageable pageable
    );
}
