package com.financia.kash.shared.infrastructure.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ReportingClockConfiguration {
    @Bean
    public Clock reportingClock(@Value("${app.reporting.time-zone:America/Lima}") String timeZone) {
        return Clock.system(ZoneId.of(timeZone));
    }
}
