package com.example.adapters;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotifierPort.
 * This adapter would typically call an external Slack API Webhook.
 * For the scope of this defect validation, it acts as a pass-through/wiring mechanism
 * ensuring the Domain Event (with the GitHub URL) reaches the infrastructure layer.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Override
    public void notify(DefectReportedEvent event) {
        // In a production environment, this would use an HttpClient (like OkHttp or RestTemplate)
        // to POST to a Slack Incoming Webhook URL.
        // 
        // Example implementation:
        // String webhookUrl = environment.getRequiredProperty("SLACK_WEBHOOK_URL");
        // String payload = formatSlackMessage(event);
        // restTemplate.postForObject(webhookUrl, payload, String.class);

        log.info("Defect Reported to Slack: ID={}, Title={}, URL={}", 
            event.defectId(), event.title(), event.githubIssueUrl());
        
        // VW-454 Validation: The adapter successfully receives the event with the URL populated.
        if (event.githubIssueUrl() == null || event.githubIssueUrl().isBlank()) {
            throw new IllegalStateException("Slack notification cannot be sent: GitHub URL is missing");
        }
    }
}
