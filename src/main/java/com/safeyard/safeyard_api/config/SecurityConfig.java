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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthFilter authFilter;               // filtro JWT
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
                        // auth público
                        .requestMatchers("/api/auth/**").permitAll()

                        // integrations públicos
                        .requestMatchers("/api/integrations/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/integrations/events").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/integrations/events").permitAll()

                        // preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // regras de domínio (usando ADMIN / FUNCIONARIO / CLIENTE)
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
                // API não redireciona, devolve 401/403
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(401))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(403))
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ===================== WEB (Thymeleaf) =====================
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
                        // actuator / swagger
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // estáticos / dev
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                        .requestMatchers("/files/**").permitAll()

                        // público
                        .requestMatchers("/", "/index", "/login", "/error", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // áreas autenticadas
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

    // ===================== UserDetailsService =====================
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.isAtivo(),
                    true,
                    true,
                    true,
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        };
    }

    // ===================== PasswordEncoder =====================
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ===================== AuthenticationManager =====================
    @Bean
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }
}
