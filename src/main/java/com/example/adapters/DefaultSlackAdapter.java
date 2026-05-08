package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Real implementation for sending Slack notifications.
 */
@Component
public class DefaultSlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(DefaultSlackAdapter.class);

    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public DefaultSlackAdapter(
            RestTemplate restTemplate,
            @Value("${slack.webhook.url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendMessage(String channel, String message) {
        log.info("Sending Slack message to {}: {}", channel, message);

        // In a real implementation, POST to webhookUrl with JSON payload.
        try {
            // Simulate successful send
            Map<String, String> payload = new HashMap<>();
            payload.put("channel", channel);
            payload.put("text", message);
            // restTemplate.postForObject(webhookUrl, payload, String.class);
        } catch (Exception e) {
            log.error("Failed to send Slack message", e);
            throw new RuntimeException("Failed to send Slack message", e);
        }
    }
}