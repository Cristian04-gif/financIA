package com.financia.kash.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI kashOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kash API")
                        .version("0.0.1")
                        .description("API REST para gestion financiera personal.")
                        .contact(new Contact()
                                .name("Equipo FinancIA")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor local"))
                .schemaRequirement(BEARER_AUTH, new SecurityScheme()
                        .name(BEARER_AUTH)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
