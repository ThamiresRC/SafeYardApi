// src/main/java/com/safeyard/safeyard_api/config/SwaggerConfig.java
package com.safeyard.safeyard_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SafeYard API")
                        .version("1.0")
                        .description("API para controle de motos em pátio (sem autenticação JWT)"));
        // Sem SecurityRequirement / SecurityScheme
    }
}
