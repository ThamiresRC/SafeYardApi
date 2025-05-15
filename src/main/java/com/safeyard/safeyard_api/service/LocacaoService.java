package com.safeyard.safeyard_api.service;


import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
  
import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Locacao;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.LocacaoRepository;
import com.safeyard.safeyard_api.repository.MotoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocacaoService {

    private final LocacaoRepository repository;
    private final ClienteRepository clienteRepository;
    private final MotoRepository motoRepository;

    public LocacaoDTO create(LocacaoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        Moto moto = motoRepository.findById(dto.motoId())
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        Locacao locacao = Locacao.builder()
                .dataSaida(dto.dataSaida())
                .dataDevolucao(dto.dataDevolucao())
                .condicaoEntrega(dto.condicaoEntrega())
                .condicaoDevolucao(dto.condicaoDevolucao())
                .qrCode(dto.qrCode())
                .cliente(cliente)
                .moto(moto)
                .build();

        return toDTO(repository.save(locacao));
    }

   public Page<LocacaoDTO> findByFilters(Long clienteId, Long motoId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
    return repository.findByFilters(clienteId, motoId, inicio, fim, pageable).map(this::toDTO);
}

    private LocacaoDTO toDTO(Locacao l) {
        return new LocacaoDTO(
                l.getId(),
                l.getDataSaida(),
                l.getDataDevolucao(),
                l.getCondicaoEntrega(),
                l.getCondicaoDevolucao(),
                l.getQrCode(),
                l.getCliente().getId(),
                l.getMoto().getId()
        );
    }
}
