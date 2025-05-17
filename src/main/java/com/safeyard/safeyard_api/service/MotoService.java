package com.safeyard.safeyard_api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public String salvarImagem(Long id, MultipartFile file) {
    Moto moto = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

    String nomeArquivo = "placa_" + moto.getPlaca() + "_" + System.currentTimeMillis() + ".jpg";
    Path destino = Paths.get("upload", nomeArquivo);

    try {
        Files.createDirectories(destino.getParent());
        file.transferTo(destino);
    } catch (IOException e) {
        throw new RuntimeException("Erro ao salvar imagem", e);
    }

    moto.setImagemPath(destino.toString());
    repository.save(moto);
    return destino.toString();
}

}
