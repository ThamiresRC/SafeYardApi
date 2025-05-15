package com.safeyard.safeyard_api.repository;


import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.safeyard.safeyard_api.model.Locacao;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {

    @Query("""
        SELECT l FROM Locacao l
        WHERE (:clienteId IS NULL OR l.cliente.id = :clienteId)
        AND (:motoId IS NULL OR l.moto.id = :motoId)
        AND (:inicio IS NULL OR l.dataSaida >= :inicio)
        AND (:fim IS NULL OR l.dataDevolucao <= :fim)
    """)
    Page<Locacao> findByFilters(
        @Param("clienteId") Long clienteId,
        @Param("motoId") Long motoId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        Pageable pageable
    );
}
