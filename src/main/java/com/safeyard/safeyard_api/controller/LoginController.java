package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "denied", required = false) String denied,
                            Model model,
                            @ModelAttribute("error") String flashError) {

        if (logout != null) {
            model.addAttribute("logoutMsg", "Você saiu com sucesso.");
        }

        if (denied != null) {
            model.addAttribute("errorMsg", "Acesso negado. Faça login.");
        }

        if (flashError != null && !flashError.isBlank()) {
            model.addAttribute("errorMsg", flashError);
        }

        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpServletRequest request,
                          RedirectAttributes ra) {

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            ra.addFlashAttribute("error", "Informe e-mail e senha.");
            return "redirect:/login";
        }

        try {
            User u = authService.authenticate(username, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("userId",    u.getId());
            session.setAttribute("userEmail", u.getEmail());
            session.setAttribute("userName",
                    (u.getNome() != null && !u.getNome().isBlank())
                            ? u.getNome()
                            : u.getEmail()
            );
            session.setAttribute("userRole",  u.getRole().name());

            if ("CLIENTE".equals(u.getRole().name())) {
                return "redirect:/cliente/area";
            } else {
                return "redirect:/dashboard";
            }

        } catch (EntityNotFoundException ex) {
            ra.addFlashAttribute("error", "Usuário não encontrado.");
            return "redirect:/login";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String doLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout";
    }
}
