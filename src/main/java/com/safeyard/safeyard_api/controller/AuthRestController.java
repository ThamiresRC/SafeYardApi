package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Usuário ou senha inválidos."));
        } catch (RuntimeException ex) {
            log.error("[LOGIN-API] Erro ao autenticar", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno ao autenticar."));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest body) {
        try {
            if (body.getNome() == null || body.getNome().isBlank()
                    || body.getCpf() == null || body.getCpf().isBlank()
                    || body.getEmail() == null || body.getEmail().isBlank()
                    || body.getSenha() == null || body.getSenha().isBlank()
                    || body.getConfirmacaoSenha() == null || body.getConfirmacaoSenha().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Preencha todos os campos obrigatórios."));
            }

            if (!body.getSenha().equals(body.getConfirmacaoSenha())) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("As senhas não coincidem."));
            }

            String cpfLimpo = body.getCpf().replaceAll("\\D", "");

            User u = authService.registerCliente(
                    body.getNome().trim(),
                    cpfLimpo,
                    body.getEmail().trim(),
                    body.getSenha()
            );

            RegisterResponse resp = new RegisterResponse(
                    u.getId(),
                    u.getEmail(),
                    u.getNome(),
                    u.getRole().name()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(resp);

        } catch (DataIntegrityViolationException ex) {
            log.warn("[REGISTER-API] Violação de integridade ao registrar cliente", ex);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("CPF ou e-mail já cadastrado."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            log.error("[REGISTER-API] Erro interno ao registrar cliente", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno ao registrar cliente."));
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
    public static class RegisterRequest {
        private String nome;
        private String cpf;
        private String email;
        private String senha;
        private String confirmacaoSenha;
    }

    @Data
    @AllArgsConstructor
    public static class RegisterResponse {
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
