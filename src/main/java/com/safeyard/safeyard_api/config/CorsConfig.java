package com.safeyard.safeyard_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Use patterns para cobrir várias portas/IPs (Expo, emulador, web, rede local)
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:*",       // Expo Web / Metro local
                "http://127.0.0.1:*",
                "http://10.0.2.2:*",        // Emulador Android (AVD) acessando seu PC
                "http://192.168.*.*:*"      // Celular real na mesma rede (ajuste se quiser ser mais estrito)
        ));

        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
