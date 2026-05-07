package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real adapter for Slack Notifications.
 * Posts messages to the configured Slack Webhook URL.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    private final String webhookUrl;
    private final RestClient restClient;

    public SlackNotificationAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.restClient = RestClient.create();
    }

    @Override
    public void sendNotification(String channel, String body) {
        try {
            // Structure the payload for Slack API
            String payload = String.format("{\"text\": \"%s\", \"channel\": \"%s\"}", escapeJson(body), channel);
            
            log.info("Sending Slack notification to channel {}: {}", channel, body);
            
            // Execute POST
            // Note: RestClient is part of Spring Boot 3.2+ (RestTemplate replacement)
            String response = this.restClient.post()
                .uri(webhookUrl)
                .body(payload)
                .retrieve()
                .body(String.class);
                
            log.debug("Slack response: {}", response);
        } catch (Exception e) {
            // In a real banking env, this might go to a DLQ or secondary alert system
            // For now, we log the error so the temporal workflow doesn't fail permanently if Slack is down
            log.error("Failed to send Slack notification to {}", channel, e);
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }
}
