package com.safeyard.safeyard_api.service;

import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.model.UserRole;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String email, String rawPassword) {
        var user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (!user.isAtivo()) {
            throw new RuntimeException("Usuário inativo.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getSenha())) {
            throw new RuntimeException("Senha inválida.");
        }

        return user;
    }

    @Transactional
    public User registerCliente(String nome, String cpf, String email, String rawPassword) {

        String emailLower = email.toLowerCase();
        String cpfDigits  = cpf.replaceAll("\\D", "");

        boolean existsUserEmail    = userRepository.findByEmail(emailLower).isPresent();
        boolean existsClienteEmail = clienteRepository.findByEmailIgnoreCase(emailLower).isPresent();
        boolean existsClienteCpf   = clienteRepository.findByCpf(cpfDigits).isPresent();

        if (existsUserEmail || existsClienteEmail || existsClienteCpf) {
            throw new DataIntegrityViolationException("CPF ou e-mail já cadastrado.");
        }

        Cliente cliente = Cliente.builder()
                .nome(nome)
                .cpf(cpfDigits)
                .email(emailLower)
                .build();
        clienteRepository.save(cliente);

        User user = new User();
        user.setNome(nome);
        user.setEmail(emailLower);
        user.setSenha(passwordEncoder.encode(rawPassword));
        user.setRole(UserRole.CLIENTE);
        user.setAtivo(true);

        return userRepository.save(user);
    }
}
