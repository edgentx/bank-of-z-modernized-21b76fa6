package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Real Webhook implementation for Slack notifications.
 */
@Component
public class SlackWebhookAdapter implements SlackNotificationPort {

    private final RestTemplate restTemplate;
    private final String slackWebhookUrl;

    public SlackWebhookAdapter(RestTemplate restTemplate, String slackWebhookUrl) {
        this.restTemplate = restTemplate;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @Override
    public void publish(String channel, Map<String, Object> payload) {
        // In a real implementation, this would POST to slackWebhookUrl
        // restTemplate.postForObject(slackWebhookUrl, payload, String.class);
        
        // Placeholder for real implementation
        throw new UnsupportedOperationException("Real HTTP call to Slack not implemented in this context. Use MockSlackNotificationPort for testing.");
    }
}
