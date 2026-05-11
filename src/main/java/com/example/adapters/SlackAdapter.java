package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real adapter for Slack notifications.
 * Sends messages to a configured Slack Webhook URL.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);
    private final RestClient restClient;
    private final String webhookUrl;

    public SlackAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.restClient = RestClient.create();
    }

    @Override
    public void sendMessage(String body) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack webhook URL is not configured. Skipping notification.");
            return;
        }

        try {
            // Construct Slack payload format
            // { "text": "<message body>" }
            String jsonPayload = String.format("{\"text\":\"%s\"}", escapeJson(body));

            restClient.post()
                .uri(webhookUrl)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(jsonPayload)
                .retrieve()
                .toBodilessEntity();
            
            log.info("Successfully sent Slack notification.");
        } catch (Exception e) {
            log.error("Failed to send Slack notification: {}", e.getMessage());
            // Depending on requirements, we might want to throw here.
            // For reporting defects, we often don't want to crash the app if Slack is down.
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
