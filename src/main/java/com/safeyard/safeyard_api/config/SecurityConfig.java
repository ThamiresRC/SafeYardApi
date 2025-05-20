package com.safeyard.safeyard_api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.safeyard.safeyard_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthFilter authFilter;
    private final UserRepository userRepository;

 @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions().disable()) // ✅ Permite o uso de frames (H2)
        .authorizeHttpRequests(auth -> auth
            // ✅ Libera Swagger
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html"
            ).permitAll()

            // ✅ Libera console H2
            .requestMatchers("/h2-console/**").permitAll()

            // ✅ Libera login
            .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()

            // ⛔️ Demais rotas exigem autenticação
            .anyRequest().authenticated()
        )
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}



   @Bean
    public UserDetailsService userDetailsService() {
    return username -> userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
}


    @Bean
    public AuthenticationManager authenticationManager() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return new ProviderManager(provider);
    }
}
