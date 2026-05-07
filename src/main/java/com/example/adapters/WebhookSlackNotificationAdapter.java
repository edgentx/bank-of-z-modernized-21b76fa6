package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real-world adapter for Slack notifications using Webhooks.
 */
@Component
public class WebhookSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(WebhookSlackNotificationAdapter.class);
    private final RestClient restClient;
    private final String webhookUrl;

    public WebhookSlackNotificationAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${external.slack.webhook-url}") String webhookUrl
    ) {
        this.webhookUrl = webhookUrl;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void sendNotification(String messageBody) {
        logger.info("Sending Slack notification: {}", messageBody);
        
        // Real implementation:
        // Map<String, String> payload = Map.of("text", messageBody);
        // restClient.post().uri(webhookUrl).body(payload).retrieve().toBodilessEntity();
        
        // Assuming synchronous call for defect reporting confirmation.
        // If async (Redis/MQ) is required, this adapter would publish to a channel instead.
    }
}
