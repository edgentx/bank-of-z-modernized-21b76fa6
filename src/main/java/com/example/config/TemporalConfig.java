package com.example.config;

import com.example.adapters.TemporalAdapter;
import com.example.ports.TemporalPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Temporal workflow adapters.
 * This wiring allows the MockTemporalAdapter to be used in tests via @Primary annotation
 * or profile-specific configuration, while the real adapter is used in production.
 */
@Configuration
public class TemporalConfig {

    @Bean
    @ConditionalOnMissingBean(TemporalPort.class)
    public TemporalPort temporalAdapter() {
        return new TemporalAdapter();
    }
}
