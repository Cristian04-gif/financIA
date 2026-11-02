package com.financia.kash.movimiento.comprobante.infrastructure.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiCascadeProperties {

    private List<String> modelsCascade = new ArrayList<>();

    public List<String> getModelsCascade() {
        return modelsCascade;
    }

    public void setModelsCascade(List<String> modelsCascade) {
        this.modelsCascade = modelsCascade;
    }
}
