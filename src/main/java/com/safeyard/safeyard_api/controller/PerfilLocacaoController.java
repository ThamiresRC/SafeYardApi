// src/main/java/com/safeyard/safeyard_api/controller/PerfilLocacaoController.java
package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.dto.LocacaoViewDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.MotoRepository;
import com.safeyard.safeyard_api.service.LocacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locacoes")
@RequiredArgsConstructor
public class PerfilLocacaoController {

    private final ClienteRepository clienteRepository;
    private final LocacaoService locacaoService;
    private final MotoRepository motoRepository;

    /** Última locação do cliente logado (se existir) */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/me/ultima")
    public LocacaoViewDTO minhaUltima(@AuthenticationPrincipal User user) {
        Cliente cli = clienteRepository.findByEmailIgnoreCase(user.getEmail())
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado para: " + user.getEmail()));

        Page<LocacaoDTO> page = locacaoService.findByCliente(
                cli.getId(),
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dataSaida"))
        );

        if (page.isEmpty()) {
            // Sem locação: retorna DTO sem placa/datas
            return new LocacaoViewDTO(null, cli.getCpf(), cli.getNome(), null, null, null);
        }

        LocacaoDTO l = page.getContent().get(0);
        Moto m = (l.motoId() != null) ? motoRepository.findById(l.motoId()).orElse(null) : null;

        return new LocacaoViewDTO(
                l.id(),
                cli.getCpf(),
                cli.getNome(),
                (m != null ? m.getPlaca() : null),
                l.dataSaida(),
                l.dataDevolucao()
        );
    }
}
