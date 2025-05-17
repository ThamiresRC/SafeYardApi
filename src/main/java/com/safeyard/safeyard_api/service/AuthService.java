package com.safeyard.safeyard_api.service;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.model.UserRole;
import com.safeyard.safeyard_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;

    public User authenticate(String email, String rawPassword) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!user.isAtivo())
            throw new RuntimeException("Usuário inativo");

        if (!new BCryptPasswordEncoder().matches(rawPassword, user.getSenha()))
            throw new RuntimeException("Senha inválida");

        return user;
    }

    public User register(String nome, String email, String senha, UserRole role) {
        User user = User.builder()
                .nome(nome)
                .email(email)
                .senha(new BCryptPasswordEncoder().encode(senha))
                .role(role)
                .ativo(true)
                .build();
        return repository.save(user);
    }
}
