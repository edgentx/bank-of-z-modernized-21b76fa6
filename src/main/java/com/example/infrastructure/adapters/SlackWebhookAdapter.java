package com.example.infrastructure.adapters;

import com.example.domain.validation.port.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real-world implementation of SlackPort using Spring's RestClient.
 * Posts messages to a configured Slack Webhook URL.
 */
@Component
public class SlackWebhookAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackWebhookAdapter.class);
    private final RestClient restClient;

    public SlackWebhookAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.restClient = RestClient.builder()
            .baseUrl(webhookUrl)
            .build();
    }

    @Override
    public void postMessage(String text) {
        try {
            // Construct Slack payload according to Webhook API specs
            // We simply send the text content.
            String response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"text\": \"" + escapeJson(text) + "\"}")
                .retrieve()
                .body(String.class);
            
            log.info("Posted message to Slack. Response: {}", response);
        } catch (Exception e) {
            // In a real batch/bank scenario, we might retry or post to DLQ
            // For now, we wrap in RuntimeException to fail the workflow as expected by test.
            throw new RuntimeException("Failed to post message to Slack", e);
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
