package com.example.configuration;

import com.example.adapters.SlackMessageValidatorImpl;
import com.example.domain.shared.SlackMessageValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Reporting components.
 * Wires the SlackMessageValidator adapter implementation.
 */
@Configuration
public class DefectReportingConfiguration {

    @Bean
    public SlackMessageValidator slackMessageValidator() {
        return new SlackMessageValidatorImpl();
    }
}
