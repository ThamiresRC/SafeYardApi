package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.service.ClienteService;
import com.safeyard.safeyard_api.service.LocacaoService;
import com.safeyard.safeyard_api.service.MotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ClienteService clienteService;
    private final MotoService motoService;
    private final LocacaoService locacaoService;

    @GetMapping("/")
    public String root() { return "redirect:/dashboard"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("username", username(principal));
        model.addAttribute("totalClientes", clienteService.count());
        model.addAttribute("totalMotos", motoService.count());
        model.addAttribute("totalLocacoes", locacaoService.count());
        return "dashboard";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/clientes")
    public String clientes(Model model, Principal principal) {
        model.addAttribute("username", username(principal));
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/relatorios")
    public String relatorios(Model model, Principal principal) {
        model.addAttribute("username", username(principal));
        return "relatorios";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/locacoes/fechar-ativas")
    public String fecharLocacoesAtivas(RedirectAttributes ra) {
        int qtd = locacaoService.fecharTodasAtivas();
        ra.addFlashAttribute("msg", (qtd == 0)
                ? "Não havia locações ativas para finalizar."
                : ("Foram finalizadas " + qtd + " locação(ões) ativa(s)."));
        return "redirect:/locacoes";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/motos")
    public String motos(Model model, Principal principal, Pageable pageable) {
        model.addAttribute("username", username(principal));
        Page<MotoDTO> page = motoService.findAll(pageable);
        model.addAttribute("motos", page.getContent());
        model.addAttribute("page", page);
        return "motos/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/motos/nova")
    public String novaMoto(Model model, Principal principal) {
        model.addAttribute("username", username(principal));
        model.addAttribute("titulo", "Nova moto");
        model.addAttribute("acao", "/motos/salvar");
        model.addAttribute("moto", MotoDTO.vazio());
        return "motos/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @PostMapping("/motos/salvar")
    public String salvarNova(@Valid @ModelAttribute("moto") MotoDTO dto,
                             BindingResult br,
                             Model model,
                             Principal principal,
                             RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("username", username(principal));
            model.addAttribute("titulo", "Nova moto");
            model.addAttribute("acao", "/motos/salvar");
            return "motos/form";
        }
        try {
            motoService.create(dto);
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            addFieldErrorFromMessage(e, br);
            model.addAttribute("username", username(principal));
            model.addAttribute("titulo", "Nova moto");
            model.addAttribute("acao", "/motos/salvar");
            return "motos/form";
        }
        ra.addFlashAttribute("msg", "Moto criada com sucesso.");
        return "redirect:/motos";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/motos/{id}/editar")
    public String editarMoto(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("username", username(principal));
        model.addAttribute("titulo", "Editar moto");
        model.addAttribute("acao", "/motos/" + id + "/salvar");
        model.addAttribute("moto", motoService.findById(id));
        return "motos/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @PostMapping("/motos/{id}/salvar")
    public String salvarEdicao(@PathVariable Long id,
                               @Valid @ModelAttribute("moto") MotoDTO dto,
                               BindingResult br,
                               Model model,
                               Principal principal,
                               RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("username", username(principal));
            model.addAttribute("titulo", "Editar moto");
            model.addAttribute("acao", "/motos/" + id + "/salvar");
            return "motos/form";
        }
        try {
            motoService.update(id, dto);
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            addFieldErrorFromMessage(e, br);
            model.addAttribute("username", username(principal));
            model.addAttribute("titulo", "Editar moto");
            model.addAttribute("acao", "/motos/" + id + "/salvar");
            return "motos/form";
        }
        ra.addFlashAttribute("msg", "Moto atualizada com sucesso.");
        return "redirect:/motos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/motos/{id}/excluir")
    public String confirmarExclusao(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("username", username(principal));
        model.addAttribute("moto", motoService.findById(id));
        return "motos/confirm-delete";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/motos/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        motoService.delete(id);
        ra.addFlashAttribute("msg", "Moto excluída com sucesso.");
        return "redirect:/motos";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/motos/{id}/upload")
    public String uploadForm(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("username", username(principal));
        model.addAttribute("titulo", "Upload de foto");
        model.addAttribute("moto", motoService.findById(id));
        return "motos/upload";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @PostMapping(value = "/motos/{id}/upload", consumes = "multipart/form-data")
    public String upload(@PathVariable Long id,
                         @RequestParam("file") MultipartFile file,
                         RedirectAttributes ra) {
        if (file == null || file.isEmpty()) {
            ra.addFlashAttribute("error", "Selecione um arquivo de imagem.");
            return "redirect:/motos/" + id + "/upload";
        }
        try {
            String urlPublica = motoService.salvarImagem(id, file);
            ra.addFlashAttribute("msg", "Upload realizado com sucesso.");
            ra.addFlashAttribute("fotoUrl", urlPublica);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Falha ao enviar a foto: " + e.getMessage());
            return "redirect:/motos/" + id + "/upload";
        }
        return "redirect:/motos";
    }

    private String username(Principal principal) {
        return (principal != null && principal.getName() != null) ? principal.getName() : "Usuário";
    }

    private void addFieldErrorFromMessage(Exception e, BindingResult br) {
        String msg = String.valueOf(e.getMessage()).toLowerCase();
        if (msg.contains("placa")) {
            br.addError(new FieldError("moto", "placa", "Esta placa já está cadastrada."));
        } else if (msg.contains("chassi")) {
            br.addError(new FieldError("moto", "chassi", "Este chassi já está cadastrado."));
        } else {
            br.addError(new FieldError("moto", "placa", "Registro já existente."));
        }
    }
}
