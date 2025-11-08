package com.safeyard.safeyard_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("errorMsg", "Usuário ou senha inválidos.");
        if (logout != null) model.addAttribute("logoutMsg", "Você saiu com sucesso.");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpServletRequest request,
                          Model model) {
        // Validação mínima (coloque sua regra real se quiser)
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("errorMsg", "Informe e-mail e senha.");
            return "login";
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("userName", username);
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String doLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/login?logout";
    }
}
