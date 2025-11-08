package com.safeyard.safeyard_api.config;

import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.model.Moto.StatusMoto;
import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.model.UserRole;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.MotoRepository;
import com.safeyard.safeyard_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Profile({"dev","prod-local"})
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final MotoRepository motoRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[SEED] Iniciando...");
        seedUsers();
        seedClientes();
        seedMotos();
        log.info("[SEED] Finalizado.");
    }

    private void seedUsers() {
        createUserIfMissing("admin@safeyard.com",   "Administrador", "admin123",   UserRole.ADMIN,       true);
        createUserIfMissing("func@safeyard.com",    "Funcionário",   "func123",    UserRole.FUNCIONARIO, true);
        createUserIfMissing("cliente@safeyard.com", "Cliente Demo",  "cliente123", UserRole.CLIENTE,     true);
    }

    private void createUserIfMissing(String email, String nome, String senhaPura, UserRole role, boolean ativo) {
        userRepository.findByEmail(email.toLowerCase()).ifPresentOrElse(
                u -> log.info("[SEED][USERS] já existe: {} ({})", email, role),
                () -> {
                    User novo = User.builder()
                            .nome(nome)
                            .email(email.toLowerCase())
                            .senha(passwordEncoder.encode(senhaPura))
                            .role(role)
                            .ativo(ativo)
                            .build();
                    userRepository.save(novo);
                    log.info("[SEED][USERS] criado: {} ({})", email, role);
                }
        );
    }

    private void seedClientes() {
        if (clienteRepository.count() > 0) return;
        clienteRepository.saveAll(List.of(
                new Cliente(null, "João Silva",  "11122233344", "joao@email.com"),
                new Cliente(null, "Maria Souza", "22233344455", "maria@email.com"),
                new Cliente(null, "Carlos Lima", "33344455566", "carlos@email.com"),
                new Cliente(null, "Ana Costa",   "44455566677", "ana@email.com")
        ));
    }

    private void seedMotos() {
        if (motoRepository.count() > 0) return;
        motoRepository.saveAll(List.of(
                Moto.builder().placa("ABC1D23").modelo("Honda CG 160").chassi("9BWZZZ377VT000111").status(StatusMoto.DISPONIVEL).build(),
                Moto.builder().placa("EFG2H34").modelo("Yamaha Fazer 250").chassi("9BWZZZ377VT000222").status(StatusMoto.DISPONIVEL).build(),
                Moto.builder().placa("JKL3M45").modelo("Honda Biz 125").chassi("9BWZZZ377VT000333").status(StatusMoto.EM_USO).build()
        ));
    }
}
