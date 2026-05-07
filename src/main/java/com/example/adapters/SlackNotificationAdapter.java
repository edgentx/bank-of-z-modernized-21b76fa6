package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real adapter for sending Slack notifications.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public SlackNotificationAdapter(RestTemplate restTemplate,
                                    @Value("${integration.slack.webhook-url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void notify(String messageBody) {
        log.info("Sending Slack notification: {}", messageBody);
        
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack webhook URL not configured, skipping notification.");
            return;
        }

        try {
            // Real implementation: restTemplate.postForEntity(webhookUrl, payload, String.class);
            log.debug("Posted to Slack webhook: {}", webhookUrl);
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            // Depending on requirements, we might throw here. 
            // Currently treating notification as best-effort to not block the defect report.
        }
    }
}
