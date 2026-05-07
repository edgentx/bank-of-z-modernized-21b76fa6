package com.example.config;

import com.example.adapters.SlackWebhookAdapter;
import com.example.adapters.VForce360HttpAdapter;
import com.example.application.ReportDefectWorkflowService;
import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Port;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for the Defect Reporting context.
 * Wires the Service with the appropriate Adapters (Real or Mock).
 */
@Configuration
public class DefectReportingConfig {

    /**
     * RestTemplate for HTTP clients.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Real implementation of VForce360Port.
     * Activated when 'vforce360.adapter.type=real' (or default if not mocked).
     */
    @Bean
    @ConditionalOnProperty(name = "vforce360.adapter.type", havingValue = "real", matchIfMissing = true)
    public VForce360Port vForce360Port(RestTemplate restTemplate) {
        String baseUrl = "https://vforce360.example.com/api"; // Default or from config
        return new VForce360HttpAdapter(restTemplate, baseUrl);
    }

    /**
     * Real implementation of SlackNotificationPort.
     */
    @Bean
    @ConditionalOnProperty(name = "slack.adapter.type", havingValue = "real", matchIfMissing = true)
    public SlackNotificationPort slackNotificationPort(RestTemplate restTemplate) {
        String webhookUrl = "https://hooks.slack.com/services/..."; // Default or from config
        return new SlackWebhookAdapter(restTemplate, webhookUrl);
    }

    /**
     * The Service orchestrating the workflow.
     * Injects the ports defined above.
     */
    @Bean
    public ReportDefectWorkflowService reportDefectWorkflowService(
        VForce360Port vForce360Port,
        SlackNotificationPort slackNotificationPort
    ) {
        return new ReportDefectWorkflowService(vForce360Port, slackNotificationPort);
    }
}
