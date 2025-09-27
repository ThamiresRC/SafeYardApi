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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final MotoRepository motoRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedClientes();
        seedMotos();
    }

    private void seedUsers() {
        createUserIfMissing("admin@safeyard.com", "Admin",        "123456", UserRole.ADMIN,        true);
        createUserIfMissing("func@safeyard.com",  "Funcionário",  "123456", UserRole.FUNCIONARIO,  true);

        createUserIfMissing("cliente@safeyard.com", "Cliente Demo", "123456", UserRole.CLIENTE, true);
    }

    private void createUserIfMissing(String email, String nome, String rawPassword, UserRole role, boolean ativo) {
        if (userRepository.findByEmail(email).isPresent()) return;

        User u = User.builder()
                .nome(nome)
                .email(email.toLowerCase())
                .senha(passwordEncoder.encode(rawPassword))
                .role(role)
                .ativo(ativo)
                .build();

        userRepository.save(u);
        System.out.println("Usuário seed criado: " + email + " [" + role + "]");
    }


    private void seedClientes() {
        if (clienteRepository.count() > 0) return;

        clienteRepository.saveAll(List.of(
                new Cliente(null, "João Silva",  "11122233344", "joao@email.com"),
                new Cliente(null, "Maria Souza", "22233344455", "maria@email.com"),
                new Cliente(null, "Carlos Lima", "33344455566", "carlos@email.com"),
                new Cliente(null, "Ana Costa",   "44455566677", "ana@email.com"),
                new Cliente(null, "Paulo Dias",  "55566677788", "paulo@email.com"),
                new Cliente(null, "Cliente Demo", "99988877766", "cliente@safeyard.com")
        ));
        System.out.println("Clientes seed inseridos");
    }

    private void seedMotos() {
        if (motoRepository.count() > 0) return;
        motoRepository.saveAll(List.of(
                Moto.builder()
                        .placa("ABC1D23")
                        .modelo("Honda CG 160")
                        .chassi("9BWZZZ377VT000111")
                        .status(StatusMoto.DISPONIVEL)
                        .build(),
                Moto.builder()
                        .placa("EFG2H34")
                        .modelo("Yamaha Fazer 250")
                        .chassi("9BWZZZ377VT000222")
                        .status(StatusMoto.DISPONIVEL)
                        .build(),
                Moto.builder()
                        .placa("JKL3M45")
                        .modelo("Honda Biz 125")
                        .chassi("9BWZZZ377VT000333")
                        .status(StatusMoto.EM_USO)
                        .build(),
                Moto.builder()
                        .placa("NOP4Q56")
                        .modelo("Honda XRE 300")
                        .chassi("9BWZZZ377VT000444")
                        .status(StatusMoto.MANUTENCAO)
                        .build(),
                Moto.builder()
                        .placa("RST5U67")
                        .modelo("Suzuki Yes 125")
                        .chassi("9BWZZZ377VT000555")
                        .status(StatusMoto.DISPONIVEL)
                        .build()
        ));
        System.out.println("Motos seed inseridas");
    }
}
