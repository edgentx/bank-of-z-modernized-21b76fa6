package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for Defect Reporting components.
 */
@Configuration
public class DefectReportingConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
