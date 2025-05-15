package com.safeyard.safeyard_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.repository.MotoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MotoService {

    private final MotoRepository repository;

    public MotoDTO create(MotoDTO dto) {
        Moto moto = Moto.builder()
                .placa(dto.placa())
                .modelo(dto.modelo())
                .chassi(dto.chassi())
                .status(dto.status())
                .build();
        return toDTO(repository.save(moto));
    }

    public MotoDTO update(Long id, MotoDTO dto) {
        Moto moto = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));
        moto.setModelo(dto.modelo());
        moto.setChassi(dto.chassi());
        moto.setStatus(dto.status());
        return toDTO(repository.save(moto));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<MotoDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    public MotoDTO findById(Long id) {
        return toDTO(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Moto não encontrada")));
    }

    private MotoDTO toDTO(Moto moto) {
        return new MotoDTO(moto.getId(), moto.getPlaca(), moto.getModelo(), moto.getChassi(), moto.getStatus());
    }
}
