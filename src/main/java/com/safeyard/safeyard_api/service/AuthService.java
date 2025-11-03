package com.safeyard.safeyard_api.service;

import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.model.UserRole;
import com.safeyard.safeyard_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User authenticate(String email, String rawPassword) {
        String normalizedEmail = (email == null) ? "" : email.trim().toLowerCase();

        User user = repository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!user.isAtivo()) {
            throw new RuntimeException("Usuário inativo");
        }

        if (!passwordEncoder.matches(rawPassword, user.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return user;
    }

    @Transactional
    public User register(String nome, String email, String senha, UserRole role) {
        String normalizedEmail = (email == null) ? "" : email.trim().toLowerCase();

        String hash = passwordEncoder.encode(senha);

        User user = User.builder()
                .nome(nome)
                .email(normalizedEmail)
                .senha(hash)
                .role(role)
                .ativo(true)
                .build();

        return repository.save(user);
    }
}
