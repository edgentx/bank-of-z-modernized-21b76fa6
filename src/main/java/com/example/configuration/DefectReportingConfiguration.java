package com.example.configuration;

import com.example.domain.shared.SlackMessageValidator;
import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.adapters.WebhookSlackNotificationAdapter;

/**
 * Configuration for Defect Reporting components.
 * Wires the ports and adapters together.
 */
@Configuration
public class DefectReportingConfiguration {

    @Bean
    public SlackMessageValidator slackMessageValidator() {
        // Basic validator ensuring non-empty and URL presence
        return message -> message != null && !message.isBlank() && message.contains("http");
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(SlackMessageValidator validator) {
        return new WebhookSlackNotificationAdapter(validator);
    }
}