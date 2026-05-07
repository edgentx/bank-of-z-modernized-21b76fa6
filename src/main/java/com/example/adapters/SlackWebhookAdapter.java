package com.example.adapters;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Adapter for posting notifications to Slack.
 * This implementation addresses the compiler errors and satisfies the requirements for VW-454.
 */
@Component
public class SlackWebhookAdapter {

    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public SlackWebhookAdapter(RestTemplate restTemplate, String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    public void sendNotification(String message) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            throw new IllegalStateException("Slack webhook URL is not configured.");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construct a basic Slack payload
        String payload = "{\"text\": \"" + message + "\"}";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        restTemplate.postForObject(webhookUrl, request, String.class);
    }
}
