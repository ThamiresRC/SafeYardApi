package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.ClienteProfileDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Locacao;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.LocacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ClienteProfileController {

    private final ClienteRepository clienteRepository;
    private final LocacaoRepository locacaoRepository;

    @GetMapping("/me")
    public ClienteProfileDTO me(@RequestParam("email") String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }

        String emailLower = email.trim().toLowerCase();

        Cliente c = clienteRepository.findByEmailIgnoreCase(emailLower)
                .orElseThrow(() -> new IllegalStateException(
                        "Cliente não encontrado para o e-mail: " + emailLower));

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
