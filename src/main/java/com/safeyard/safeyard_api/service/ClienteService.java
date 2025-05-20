package com.safeyard.safeyard_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.safeyard.safeyard_api.dto.ClienteDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    @CacheEvict(value = "clientes", allEntries = true)
    public ClienteDTO create(ClienteDTO dto) {
        Cliente cliente = Cliente.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .build();
        return toDTO(repository.save(cliente));
    }

    @CacheEvict(value = {"clientes", "clienteById"}, allEntries = true)
    public ClienteDTO update(Long id, ClienteDTO dto) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email());
        return toDTO(repository.save(cliente));
    }

    @CacheEvict(value = {"clientes", "clienteById"}, allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Cacheable("clientes")
    public Page<ClienteDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Cacheable(value = "clienteById", key = "#id")
    public ClienteDTO findById(Long id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado")));
    }

    private ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getCpf(), cliente.getEmail());
    }
}

