package com.safeyard.safeyard_api.repository;

import com.safeyard.safeyard_api.model.Locacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {

    @Query("""
        SELECT l FROM Locacao l
        WHERE (:clienteId IS NULL OR l.cliente.id = :clienteId)
          AND (:motoId    IS NULL OR l.moto.id    = :motoId)
          AND l.dataSaida >= COALESCE(:inicio, l.dataSaida)
              
          AND (l.dataDevolucao IS NULL OR l.dataDevolucao <= COALESCE(:fim, l.dataDevolucao))
        ORDER BY l.dataSaida DESC, l.id DESC
    """)
    Page<Locacao> findByFilters(@Param("clienteId") Long clienteId,
                                @Param("motoId") Long motoId,
                                @Param("inicio") LocalDateTime inicio,
                                @Param("fim") LocalDateTime fim,
                                Pageable pageable);

    @Query("SELECT l FROM Locacao l WHERE l.dataDevolucao IS NULL ORDER BY l.dataSaida DESC")
    Page<Locacao> findAtivas(Pageable pageable);

    List<Locacao> findByDataDevolucaoIsNull();

    Page<Locacao> findByClienteIdOrderByDataSaidaDesc(Long clienteId, Pageable pageable);

    Page<Locacao> findByClienteId(Long clienteId, Pageable pageable);

    boolean existsByMotoIdAndDataDevolucaoIsNull(Long motoId);

    Optional<Locacao> findFirstByMotoIdAndDataDevolucaoIsNullOrderByDataSaidaDesc(Long motoId);

    long countByDataDevolucaoIsNull();

    long countByClienteId(Long clienteId);

    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END
        FROM Locacao l
        WHERE l.moto.id = :motoId
          AND l.dataSaida <= :fim
          AND (l.dataDevolucao IS NULL OR l.dataDevolucao > :inicio)
    """)
    boolean existsOverlapForMoto(@Param("motoId") Long motoId,
                                 @Param("inicio") LocalDateTime inicio,
                                 @Param("fim") LocalDateTime fim);

    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END
        FROM Locacao l
        WHERE l.moto.id = :motoId
          AND l.id <> :ignoreId
          AND l.dataSaida <= :fim
          AND (l.dataDevolucao IS NULL OR l.dataDevolucao > :inicio)
    """)
    boolean existsOverlapForMotoExcludingId(@Param("motoId") Long motoId,
                                            @Param("ignoreId") Long ignoreId,
                                            @Param("inicio") LocalDateTime inicio,
                                            @Param("fim") LocalDateTime fim);

    boolean existsByIdAndClienteId(Long id, Long clienteId);

    Optional<Locacao> findByIdAndClienteId(Long id, Long clienteId);

    Page<Locacao> findByDataSaidaBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
