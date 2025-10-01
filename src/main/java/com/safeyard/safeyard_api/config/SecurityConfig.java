// src/main/java/com/safeyard/safeyard_api/config/SecurityConfig.java
package com.safeyard.safeyard_api.config;

import com.safeyard.safeyard_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthFilter authFilter;
    private final UserRepository userRepository;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/profile/me").hasRole("CLIENTE")

                        .requestMatchers(HttpMethod.GET,    "/api/clientes/**").hasAnyRole("ADMIN","FUNCIONARIO")
                        .requestMatchers(HttpMethod.POST,   "/api/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,    "/api/motos/**").hasAnyRole("ADMIN","FUNCIONARIO")
                        .requestMatchers(HttpMethod.POST,   "/api/motos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/motos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/motos/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,  "/api/locacoes/form").hasAnyRole("ADMIN","FUNCIONARIO")
                        .requestMatchers(HttpMethod.POST, "/api/locacoes/form").hasAnyRole("ADMIN","FUNCIONARIO")

                        .requestMatchers("/api/locacoes/**").hasAnyRole("ADMIN","FUNCIONARIO")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .headers(h -> h.frameOptions(fo -> fo.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers("/login", "/error", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/clientes", "/clientes/**").hasAnyRole("ADMIN","FUNCIONARIO")
                        .requestMatchers("/motos", "/motos/**").hasAnyRole("ADMIN","FUNCIONARIO")
                        .requestMatchers("/locacoes", "/locacoes/**").hasAnyRole("ADMIN","FUNCIONARIO")
                        .requestMatchers("/relatorios", "/relatorios/**").hasRole("ADMIN")
                        .requestMatchers("/cliente/**").hasRole("CLIENTE")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(e -> e.accessDeniedPage("/error"))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username ->
                userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }
}
