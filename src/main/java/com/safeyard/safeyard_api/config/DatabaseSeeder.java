package com.safeyard.safeyard_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.model.User;
import com.safeyard.safeyard_api.model.UserRole;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.MotoRepository;
import com.safeyard.safeyard_api.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder {

    private final ClienteRepository clienteRepository;
    private final MotoRepository motoRepository;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void seed() {
        if (clienteRepository.count() == 0) {
            clienteRepository.save(new Cliente(null, "João Silva", "11122233344", "joao@email.com"));
            clienteRepository.save(new Cliente(null, "Maria Souza", "22233344455", "maria@email.com"));
            clienteRepository.save(new Cliente(null, "Carlos Lima", "33344455566", "carlos@email.com"));
            clienteRepository.save(new Cliente(null, "Ana Costa", "44455566677", "ana@email.com"));
            clienteRepository.save(new Cliente(null, "Paulo Dias", "55566677788", "paulo@email.com"));
        }

        if (motoRepository.count() == 0) {
            motoRepository.save(new Moto(null, "ABC1234", "Honda CG 160", "9C2KC1670FR123456", "Disponível", null));
            motoRepository.save(new Moto(null, "XYZ5678", "Yamaha Fazer 250", "9C6KE1210F0012345", "Disponível", null));
            motoRepository.save(new Moto(null, "JHK2345", "Honda Biz", "9C2HA0710DR045678", "Alugada", null));
            motoRepository.save(new Moto(null, "LMN3456", "Honda XRE 300", "9C2RD0710ER065432", "Manutenção", null));
            motoRepository.save(new Moto(null, "QWE4567", "Suzuki Yes 125", "9CDDH0140FR098765", "Disponível", null));
        }

        if (userRepository.count() == 0) {
            userRepository.save(
                User.builder()
                    .nome("Admin")
                    .email("admin@safeyard.com")
                    .senha(passwordEncoder.encode("123456"))
                    .role(UserRole.ADMIN)
                    .ativo(true)
                    .build()
            );
        }
    }
}
