// src/main/java/com/safeyard/safeyard_api/controller/ClienteProfileController.java
package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.ClienteProfileDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Locacao;
import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.LocacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ClienteProfileController {

    private final ClienteRepository clienteRepository;
    private final LocacaoRepository locacaoRepository;

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/me")
    public ClienteProfileDTO me(@AuthenticationPrincipal User user) {
        if (user == null) throw new IllegalStateException("Usuário não autenticado.");

        Cliente c = clienteRepository.findByEmailIgnoreCase(user.getEmail())
                .orElseThrow(() -> new IllegalStateException(
                        "Cliente não encontrado para o usuário: " + user.getEmail()));

        Page<Locacao> page = locacaoRepository
                .findByClienteIdOrderByDataSaidaDesc(c.getId(), PageRequest.of(0, 1));

        Locacao loc = page.isEmpty() ? null : page.getContent().get(0);

        return new ClienteProfileDTO(
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getCpf(),
                (loc != null ? loc.getId() : null),
                (loc != null && loc.getMoto() != null ? loc.getMoto().getPlaca() : null),
                (loc != null ? loc.getDataSaida() : null),
                (loc != null ? loc.getDataDevolucao() : null)
        );
    }
}
