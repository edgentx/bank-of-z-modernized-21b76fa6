package com.example.config;

import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.InMemoryDefectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Reporting components.
 * Defines the wiring for Repositories and Ports.
 */
@Configuration
public class DefectConfig {

    /**
     * Primary Defect Repository implementation.
     * Uses InMemory implementation for TDD / Green Phase speed.
     * Can be swapped for JPA implementation in later phases.
     */
    @Bean
    public DefectRepository defectRepository() {
        return new InMemoryDefectRepository();
    }
}