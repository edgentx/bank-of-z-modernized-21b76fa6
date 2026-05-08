package com.example.configuration;

import com.example.domain.defect.port.DefectRepository;
import com.example.domain.validation.port.ValidationRepository;
import com.example.infrastructure.adapters.JpaDefectRepositoryAdapter;
import com.example.infrastructure.adapters.JpaValidationRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Reporting components.
 * Binds ports to adapters.
 */
@Configuration
public class DefectReportingConfiguration {

    @Bean
    public DefectRepository defectRepository(JpaDefectRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public ValidationRepository validationRepository(JpaValidationRepositoryAdapter adapter) {
        return adapter;
    }
}
