package com.safeyard.safeyard_api.service;

import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String email, String rawPassword) {
        var user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (!user.isAtivo()) throw new RuntimeException("Usuário inativo.");

        if (!passwordEncoder.matches(rawPassword, user.getSenha()))
            throw new RuntimeException("Senha inválida.");

        return user;
    }
}
