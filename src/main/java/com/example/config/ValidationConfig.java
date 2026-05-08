package com.example.config;

import com.example.adapters.GitHubTicketingAdapter;
import com.example.adapters.SlackWebhookAdapter;
import com.example.domain.validation.ReportDefectHandler;
import com.example.ports.SlackNotifier;
import com.example.ports.TicketingSystem;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring Configuration for wiring up the Defect Reporting workflow.
 * 
 * This configuration sets up the ReportDefectHandler with the appropriate adapters.
 * It uses @ConditionalOnMissingBean to allow mocks to override these beans in tests.
 */
@Configuration
public class ValidationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public ReportDefectHandler reportDefectHandler(SlackNotifier slackNotifier, TicketingSystem ticketingSystem) {
        return new ReportDefectHandler(slackNotifier, ticketingSystem);
    }
}
