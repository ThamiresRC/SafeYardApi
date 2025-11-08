package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.LocacaoRepository;
import com.safeyard.safeyard_api.service.LocacaoService;
import com.safeyard.safeyard_api.service.MotoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/cliente")
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
    public String area(Model model, HttpServletRequest request) {
        Cliente cli = getClienteFromSession(request);
        model.addAttribute("cliente", cli);
        return "cliente/area";
    }

    @GetMapping("/locacoes")
    public String minhasLocacoes(Model model, HttpServletRequest request, Pageable pageable) {
        Cliente cli = getClienteFromSession(request);

        Page<LocacaoDTO> page = locacaoService.findByFilters(
                cli.getId(),
                null,
                null,
                null,
                pageable
        );

        model.addAttribute("cliente", cli);
        model.addAttribute("page", page);
        model.addAttribute("locacoes", page.getContent());
        return "cliente/minhas-locacoes";
    }

    @GetMapping("/locar")
    public String locarForm(Model model, HttpServletRequest request) {
        Cliente cli = getClienteFromSession(request);
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
                              HttpServletRequest request,
                              RedirectAttributes ra,
                              Model model) {

        Cliente cli = getClienteFromSession(request);

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

        LocacaoDTO segura = new LocacaoDTO(
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
            locacaoService.create(segura);
            ra.addFlashAttribute("msg", "Solicitação registrada com sucesso.");
            return "redirect:/cliente/locacoes";
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
                           HttpServletRequest request,
                           RedirectAttributes ra) {

        Cliente cli = getClienteFromSession(request);

        if (!locacaoRepository.existsByIdAndClienteId(id, cli.getId())) {
            ra.addFlashAttribute("error", "Registro não encontrado.");
            return "redirect:/cliente/locacoes";
        }

        try {
            locacaoService.devolver(id, LocalDateTime.now(), "Devolução confirmada pelo cliente");
            ra.addFlashAttribute("msg", "Devolução registrada.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Não foi possível registrar a devolução.");
        }
        return "redirect:/cliente/locacoes";
    }

    @GetMapping("/locacoes/{id}")
    public String detalhe(@PathVariable Long id,
                          HttpServletRequest request,
                          Model model,
                          RedirectAttributes ra) {

        Cliente cli = getClienteFromSession(request);

        if (!locacaoRepository.existsByIdAndClienteId(id, cli.getId())) {
            ra.addFlashAttribute("error", "Registro não encontrado.");
            return "redirect:/cliente/locacoes";
        }

        model.addAttribute("locacaoId", id);
        model.addAttribute("cliente", cli);
        return "cliente/detalhe";
    }

    private Cliente getClienteFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new IllegalStateException("Sessão expirada ou usuário não autenticado.");
        }

        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            throw new IllegalStateException("Usuário não autenticado como cliente.");
        }

        return clienteRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Cliente não encontrado para o e-mail: " + email
                ));
    }
}
