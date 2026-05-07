package com.vforce360.adapters;

import com.vforce360.ports.ModernizationReportPort;
import com.vforce360.service.ModernizationReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to wire the Port and Adapter implementations.
 * In a real environment, this would instantiate the MongoDB adapter.
 * For this fix, we wire the Service adapter to satisfy the Controller's dependency.
 */
@Configuration
public class ReportConfiguration {

    @Bean
    public ModernizationReportPort modernizationReportPort() {
        return new ModernizationReportService();
    }
}