package com.financia.kash.movimiento.comprobante.infrastructure.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.ai")
@Getter
@Setter
public class AiCascadeProperties {

    private List<String> ocrModels = new ArrayList<>();
    private List<String> analysisModels = new ArrayList<>();
}
