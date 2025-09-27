package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.LocacaoRepository;
import com.safeyard.safeyard_api.service.LocacaoService;
import com.safeyard.safeyard_api.service.MotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/cliente")
@PreAuthorize("hasRole('CLIENTE')")
@RequiredArgsConstructor
public class ClienteAreaController {

    private final ClienteRepository clienteRepository;
    private final LocacaoService locacaoService;
    private final LocacaoRepository locacaoRepository;
    private final MotoService motoService;

    @GetMapping
    public String home() {
        return "redirect:/cliente/area";
    }
    @GetMapping("/area")
    public String area(Model model, Principal principal, Pageable pageable) {
        Cliente cli = byUserEmail(principal);
        Page<LocacaoDTO> page = locacaoService.findByFilters(cli.getId(), null, null, null, pageable);

        model.addAttribute("cliente", cli);
        model.addAttribute("page", page);
        model.addAttribute("locacoes", page.getContent());
        return "cliente/minhas-locacoes";
    }

    @GetMapping("/locacoes")
    public String minhasLocacoes(Model model, Principal principal, Pageable pageable) {
        return area(model, principal, pageable);
    }

    @GetMapping("/locar")
    public String locarForm(Model model, Principal principal) {
        Cliente cli = byUserEmail(principal);
        List<MotoDTO> motosDisp = motoService.findDisponiveis();

        model.addAttribute("cliente", cli);
        model.addAttribute("motosDisponiveis", motosDisp);
        model.addAttribute("titulo", "Nova locação");
        model.addAttribute("acao", "/cliente/locar");

        if (!model.containsAttribute("locacao")) {
            model.addAttribute("locacao", new LocacaoDTO(
                    null,
                    LocalDateTime.now(),
                    null,
                    "",
                    "",
                    "",
                    null,
                    null,
                    null
            ));
        }

        return "cliente/locar";
    }

    @PostMapping("/locar")
    public String locarSubmit(@Valid @ModelAttribute("locacao") LocacaoDTO dto,
                              BindingResult br,
                              Principal principal,
                              RedirectAttributes ra,
                              Model model) {

        Cliente cli = byUserEmail(principal);

        if (dto.motoId() == null) {
            br.rejectValue("motoId", "NotNull", "Selecione uma moto.");
        }

        if (br.hasErrors()) {
            model.addAttribute("cliente", cli);
            model.addAttribute("motosDisponiveis", motoService.findDisponiveis());
            model.addAttribute("titulo", "Nova locação");
            model.addAttribute("acao", "/cliente/locar");
            return "cliente/locar";
        }

        LocalDateTime saida = (dto.dataSaida() != null) ? dto.dataSaida() : LocalDateTime.now();

        LocacaoDTO seguro = new LocacaoDTO(
                null,
                saida,
                dto.dataDevolucao(),
                dto.condicaoEntrega(),
                dto.condicaoDevolucao(),
                dto.qrCode(),
                cli.getId(),
                dto.motoId(),
                null
        );

        try {
            locacaoService.create(seguro);
            ra.addFlashAttribute("msg", "Solicitação registrada com sucesso.");
            return "redirect:/cliente/area";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("cliente", cli);
            model.addAttribute("motosDisponiveis", motoService.findDisponiveis());
            model.addAttribute("titulo", "Nova locação");
            model.addAttribute("acao", "/cliente/locar");
            model.addAttribute("error", ex.getMessage());
            return "cliente/locar";
        }
    }

    @PostMapping("/locacoes/{id}/devolver")
    public String devolver(@PathVariable Long id,
                           Principal principal,
                           RedirectAttributes ra) {

        Cliente cli = byUserEmail(principal);
        if (!locacaoRepository.existsByIdAndClienteId(id, cli.getId())) {
            ra.addFlashAttribute("error", "Registro não encontrado.");
            return "redirect:/cliente/area";
        }

        try {
            locacaoService.devolver(id, LocalDateTime.now(), "Devolução confirmada pelo cliente");
            ra.addFlashAttribute("msg", "Devolução registrada.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Não foi possível registrar a devolução.");
        }
        return "redirect:/cliente/area";
    }

    @GetMapping("/locacoes/{id}")
    public String detalhe(@PathVariable Long id,
                          Principal principal,
                          Model model,
                          RedirectAttributes ra) {
        Cliente cli = byUserEmail(principal);
        if (!locacaoRepository.existsByIdAndClienteId(id, cli.getId())) {
            ra.addFlashAttribute("error", "Registro não encontrado.");
            return "redirect:/cliente/area";
        }
        model.addAttribute("locacaoId", id);
        model.addAttribute("cliente", cli);
        return "cliente/detalhe";
    }

    private Cliente byUserEmail(Principal principal) {
        String email = principal.getName();
        return clienteRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado para o usuário: " + email));
    }
}
