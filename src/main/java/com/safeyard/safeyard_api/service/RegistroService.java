package com.safeyard.safeyard_api.service;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.safeyard.safeyard_api.dto.RegistroDTO;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.model.RegistroMotoPatio;
import com.safeyard.safeyard_api.repository.MotoRepository;
import com.safeyard.safeyard_api.repository.RegistroRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final RegistroRepository repository;
    private final MotoRepository motoRepository;

    @CacheEvict(value = "registrosFiltrados", allEntries = true)
    public RegistroDTO create(RegistroDTO dto) {
        Moto moto = motoRepository.findById(dto.motoId())
                .orElseThrow(() -> new EntityNotFoundException("Moto nao encontrada"));

        RegistroMotoPatio registro = RegistroMotoPatio.builder()
                .dataHoraRegistro(dto.dataHoraRegistro())
                .setor(dto.setor())
                .vaga(dto.vaga())
                .tipo(dto.tipo())
                .moto(moto)
                .build();

        return toDTO(repository.save(registro));
    }

    @Cacheable(
        value = "registrosFiltrados",
        key = "#motoId + '-' + #tipo + '-' + #inicio + '-' + #fim + '-' + #pageable.pageNumber"
    )
    public Page<RegistroDTO> findByFilters(Long motoId, String tipo, LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return repository.findByFilters(motoId, tipo, inicio, fim, pageable).map(this::toDTO);
    }

    private RegistroDTO toDTO(RegistroMotoPatio r) {
        return new RegistroDTO(
                r.getId(),
                r.getDataHoraRegistro(),
                r.getSetor(),
                r.getVaga(),
                r.getTipo(),
                r.getMoto().getId()
        );
    }
}
