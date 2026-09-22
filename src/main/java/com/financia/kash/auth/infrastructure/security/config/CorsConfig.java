package com.financia.kash.auth.infrastructure.security.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@ConfigurationProperties(prefix = "url")
public class CorsConfig implements WebFluxConfigurer {

    private List<String> frontend;

    private final List<String> METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    public CorsConfig(List<String> frontend) {
        this.frontend = frontend;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins(frontend.toArray(new String[0]))// aqui
                .allowedMethods(METHODS.toArray(new String[0]))
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(frontend);
        config.setAllowedMethods(METHODS);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
