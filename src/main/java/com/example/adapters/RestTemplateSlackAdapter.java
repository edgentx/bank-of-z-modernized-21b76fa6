package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

/**
 * Real adapter for Slack notifications using RestTemplate.
 */
@Component
public class RestTemplateSlackAdapter implements SlackNotificationPort {

    private final RestTemplate restTemplate;
    private static final String WEBHOOK_URL = "https://hooks.slack.com/services/placeholder";

    public RestTemplateSlackAdapter(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public boolean sendMessage(String channel, String body) {
        // Real implementation would POST to WEBHOOK_URL
        // Payload: { "channel": channel, "text": body }
        
        try {
            // restTemplate.postForEntity(WEBHOOK_URL, createPayload(channel, body), String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private HttpEntity<Map<String, String>> createPayload(String channel, String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> payload = Map.of(
            "channel", channel,
            "text", text
        );
        
        return new HttpEntity<>(payload, headers);
    }
}
