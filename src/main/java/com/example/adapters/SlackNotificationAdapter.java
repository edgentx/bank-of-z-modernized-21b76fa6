package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Real implementation of SlackNotificationPort using RestTemplate.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final RestTemplate restTemplate;
    private final String slackWebhookUrl;

    public SlackNotificationAdapter(RestTemplate restTemplate, String slackWebhookUrl) {
        this.restTemplate = restTemplate;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @Override
    public void postMessage(String channelId, String message) {
        Map<String, Object> requestBody = Map.of(
            "channel", channelId,
            "text", message
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Execute POST request. We don't necessarily need the return response for this implementation,
        // but failure will throw an exception from RestTemplate.
        restTemplate.postForObject(slackWebhookUrl, entity, String.class);
    }
}
