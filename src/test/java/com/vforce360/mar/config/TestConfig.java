package com.vforce360.mar.config;

import com.vforce360.mar.ports.MarRepositoryPort;
import com.vforce360.mocks.MockMarRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test Configuration to swap real adapters with Mocks.
 */
@TestConfiguration
public class TestConfig {

    @Bean
    public MarRepositoryPort marRepositoryPort() {
        return new MockMarRepository();
    }
}
