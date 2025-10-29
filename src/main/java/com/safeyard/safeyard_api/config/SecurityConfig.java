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

    private final AuthFilter authFilter; // seu filtro JWT (OncePerRequestFilter)
    private final UserRepository userRepository;
    private final CorsConfigurationSource corsConfigurationSource;

    // ===================== API (/api/**) =====================
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth público
                        .requestMatchers("/api/auth/**").permitAll()

                        // Integrations públicos
                        .requestMatchers("/api/integrations/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/integrations/events").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/integrations/events").permitAll()

                        // Pré-flight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Regras de domínio
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
                // IMPORTANTE: APIs não devem redirecionar para /login quando 401
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(401))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(403))
                )
                // O filtro JWT CONTINUA, mas precisa ignorar quando não há Authorization (ver passo 2)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ===================== WEB (MVC/Thymeleaf) =====================
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        return http
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .headers(h -> h.frameOptions(fo -> fo.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Actuator / Swagger
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Dev tooling / estáticos
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                        .requestMatchers("/files/**").permitAll()

                        // Público básico
                        .requestMatchers("/", "/index", "/login", "/error", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Áreas autenticadas
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

    // ===================== Auth infra =====================
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
