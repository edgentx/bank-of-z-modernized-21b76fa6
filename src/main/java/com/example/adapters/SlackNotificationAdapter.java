package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Real adapter for posting messages to Slack.
 * Implements the SlackNotificationPort interface.
 * Configured via application properties (webhook.url).
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public SlackNotificationAdapter(@Value("${slack.webhook.url}") String webhookUrl,
                                     RestTemplate restTemplate) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean postMessage(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text cannot be null or empty");
        }

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", text);

            // In a real scenario, we would map the response to a boolean success check.
            // Here we assume 2xx success.
            restTemplate.postForObject(webhookUrl, payload, String.class);
            return true;
        } catch (Exception e) {
            log.error("Failed to post message to Slack", e);
            return false;
        }
    }
}
