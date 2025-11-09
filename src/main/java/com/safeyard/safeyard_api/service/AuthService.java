package com.safeyard.safeyard_api.service;

import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.model.UserRole;
import com.safeyard.safeyard_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String email, String rawPassword) {
        if (email == null) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        String emailLower = email.trim().toLowerCase();

        var user = userRepository.findByEmail(emailLower)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (!user.isAtivo()) {
            throw new RuntimeException("Usuário inativo.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getSenha())) {
            throw new RuntimeException("Senha inválida.");
        }

        return user;
    }

    public User registerCliente(String nome, String cpf, String email, String rawPassword) {
        String emailLower = email.trim().toLowerCase();

        boolean exists = userRepository.findByEmail(emailLower).isPresent();
        if (exists) {
            throw new DataIntegrityViolationException("E-mail já cadastrado.");
        }

        User user = new User();
        user.setNome(nome);
        user.setEmail(emailLower);
        user.setSenha(passwordEncoder.encode(rawPassword));
        user.setRole(UserRole.CLIENTE);
        user.setAtivo(true);
        return userRepository.save(user);
    }
}
