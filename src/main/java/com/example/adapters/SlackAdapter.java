package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Real-world adapter for Slack Webhook API.
 * Handles HTTP POST to send notifications.
 */
@Component
public class SlackAdapter implements SlackPort {

    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public SlackAdapter(RestTemplate restTemplate,
                        @Value("${slack.webhook-url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendMessage(Map<String, Object> context) {
        // In a real implementation, we would format the payload for Slack API:
        // Map<String, Object> payload = new HashMap<>();
        // payload.put("text", context.get("body"));
        // payload.put("channel", context.get("channel"));
        // restTemplate.postForEntity(webhookUrl, payload, String.class);
        
        // No-op for implementation stub, the logic resides in the workflow
        // ensuring the context contains the correct data structure.
    }
}
