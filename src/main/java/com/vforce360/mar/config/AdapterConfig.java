package com.vforce360.mar.config;

import com.vforce360.mar.ports.MarRepositoryPort;
import com.vforce360.mar.adapters.DatabaseMarAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class to wire the Real Adapters to the Ports.
 * In a test environment, mocks are used instead of this configuration.
 */
@Configuration
public class AdapterConfig {

    /**
     * Bean definition for the real MAR Repository adapter.
     * This would handle DB2/MongoDB interactions in the production environment.
     * The interface 'MarRepositoryPort' is what the application code depends on.
     */
    @Bean
    @Primary
    @Profile("!test") // Only active if 'test' profile is NOT active
    public MarRepositoryPort marRepositoryAdapter() {
        return new DatabaseMarAdapter();
    }
}