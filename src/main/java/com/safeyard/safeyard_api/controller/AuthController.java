package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.LoginDTO;
import com.safeyard.safeyard_api.dto.LoginResponseDTO;
import com.safeyard.safeyard_api.dto.RegisterRequest;
import com.safeyard.safeyard_api.dto.RegisterResponse;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.model.UserRole;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.UserRepository;
import com.safeyard.safeyard_api.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    private final ClienteRepository clienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                loginDTO.email(), loginDTO.senha()
        );

        Authentication auth = authenticationManager.authenticate(usernamePassword);

        User user = (User) auth.getPrincipal();
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(user.getEmail(), token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        final String email = req.email().trim().toLowerCase();
        final String cpfSomenteDigitos = req.cpf().replaceAll("\\D", "");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }
        if (clienteRepository.existsByCpf(cpfSomenteDigitos)) {
            return ResponseEntity.badRequest().body("CPF já cadastrado.");
        }

        User user = new User();
        user.setEmail(email);
        user.setNome(req.nome().trim());
        user.setSenha(passwordEncoder.encode(req.senha()));
        user.setRole(UserRole.CLIENTE);
        user = userRepository.save(user);

        Cliente cliente = new Cliente();
        cliente.setNome(req.nome().trim());
        cliente.setCpf(cpfSomenteDigitos);
        cliente.setEmail(email);
        cliente = clienteRepository.save(cliente);

        RegisterResponse resp = new RegisterResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail()
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<MeDTO> me(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(new MeDTO(
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null
        ));
    }

    public record MeDTO(String email, String role) {}
}
