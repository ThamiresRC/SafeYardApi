package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.service.ClienteService;
import com.safeyard.safeyard_api.service.LocacaoService;
import com.safeyard.safeyard_api.service.MotoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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

    private String username(Principal principal, HttpSession session) {
        if (session != null) {
            Object n = session.getAttribute("userName");
            if (n != null) return String.valueOf(n);
        }
        return (principal != null && principal.getName() != null)
                ? principal.getName()
                : "Usuário";
    }

    private String role(HttpSession session) {
        if (session != null) {
            Object r = session.getAttribute("userRole");
            if (r != null) return String.valueOf(r);
            Object atalho = session.getAttribute("role");
            if (atalho != null) return String.valueOf(atalho);
        }
        return "ADMIN";
    }

    @GetMapping("/_perfil")
    public String trocarPerfil(@RequestParam String role, HttpSession session, RedirectAttributes ra) {
        session.setAttribute("role", role.toUpperCase());
        ra.addFlashAttribute("msg", "Perfil ativo: " + role.toUpperCase());
        return "redirect:/dashboard";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        model.addAttribute("totalClientes", clienteService.count());
        model.addAttribute("totalMotos", motoService.count());
        model.addAttribute("totalLocacoes", locacaoService.count());
        return "dashboard";
    }

    @GetMapping("/clientes")
    public String clientes(Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes";
    }

    @GetMapping("/relatorios")
    public String relatorios(Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        return "relatorios";
    }

    @GetMapping("/motos")
    public String motos(Model model, Principal principal, HttpSession session, Pageable pageable) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        Page<MotoDTO> page = motoService.findAll(pageable);
        model.addAttribute("motos", page.getContent());
        model.addAttribute("page", page);
        return "motos/list";
    }

    @GetMapping("/motos/nova")
    public String novaMoto(Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        model.addAttribute("titulo", "Nova moto");
        model.addAttribute("acao", "/motos/salvar");
        model.addAttribute("moto", MotoDTO.vazio());
        return "motos/form";
    }

    @PostMapping("/motos/salvar")
    public String salvarNova(@Valid @ModelAttribute("moto") MotoDTO dto,
                             BindingResult br,
                             Model model,
                             Principal principal,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("username", username(principal, session));
            model.addAttribute("role", role(session));
            model.addAttribute("titulo", "Nova moto");
            model.addAttribute("acao", "/motos/salvar");
            return "motos/form";
        }
        try {
            motoService.create(dto);
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            addFieldErrorFromMessage(e, br);
            model.addAttribute("username", username(principal, session));
            model.addAttribute("role", role(session));
            model.addAttribute("titulo", "Nova moto");
            model.addAttribute("acao", "/motos/salvar");
            return "motos/form";
        }
        ra.addFlashAttribute("msg", "Moto criada com sucesso.");
        return "redirect:/motos";
    }

    @GetMapping("/motos/{id}/editar")
    public String editarMoto(@PathVariable Long id, Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        model.addAttribute("titulo", "Editar moto");
        model.addAttribute("acao", "/motos/" + id + "/salvar");
        model.addAttribute("moto", motoService.findById(id));
        return "motos/form";
    }

    @PostMapping("/motos/{id}/salvar")
    public String salvarEdicao(@PathVariable Long id,
                               @Valid @ModelAttribute("moto") MotoDTO dto,
                               BindingResult br,
                               Model model,
                               Principal principal,
                               HttpSession session,
                               RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("username", username(principal, session));
            model.addAttribute("role", role(session));
            model.addAttribute("titulo", "Editar moto");
            model.addAttribute("acao", "/motos/" + id + "/salvar");
            return "motos/form";
        }
        try {
            motoService.update(id, dto);
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            addFieldErrorFromMessage(e, br);
            model.addAttribute("username", username(principal, session));
            model.addAttribute("role", role(session));
            model.addAttribute("titulo", "Editar moto");
            model.addAttribute("acao", "/motos/" + id + "/salvar");
            return "motos/form";
        }
        ra.addFlashAttribute("msg", "Moto atualizada com sucesso.");
        return "redirect:/motos";
    }

    @GetMapping("/motos/{id}/excluir")
    public String confirmarExclusao(@PathVariable Long id, Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        model.addAttribute("moto", motoService.findById(id));
        return "motos/confirm-delete";
    }

    @PostMapping("/motos/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        motoService.delete(id);
        ra.addFlashAttribute("msg", "Moto excluída com sucesso.");
        return "redirect:/motos";
    }

    @GetMapping("/motos/{id}/upload")
    public String uploadForm(@PathVariable Long id, Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", username(principal, session));
        model.addAttribute("role", role(session));
        model.addAttribute("titulo", "Upload de foto");
        model.addAttribute("moto", motoService.findById(id));
        return "motos/upload";
    }

    @PostMapping(value = "/motos/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
