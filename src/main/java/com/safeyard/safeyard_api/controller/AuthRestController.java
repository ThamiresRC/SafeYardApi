package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        try {
            if (body.getEmail() == null || body.getEmail().isBlank()
                    || body.getSenha() == null || body.getSenha().isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Informe e-mail e senha."));
            }

            User u = authService.authenticate(body.getEmail(), body.getSenha());

            LoginResponse resp = new LoginResponse(
                    u.getId(),
                    u.getEmail(),
                    u.getNome(),
                    u.getRole().name()
            );

            return ResponseEntity.ok(resp);

        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(401)
                    .body(new ErrorResponse("Usuário ou senha inválidos."));
        } catch (RuntimeException ex) {
            log.error("[LOGIN-API] Erro ao autenticar", ex);
            return ResponseEntity
                    .status(500)
                    .body(new ErrorResponse("Erro interno ao autenticar."));
        }
    }


    @Data
    public static class LoginRequest {
        private String email;
        private String senha;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        private Long id;
        private String email;
        private String nome;
        private String role;
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
