package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Real implementation of SlackNotificationPort using Spring RestClient.
 */
public class SlackAdapter implements SlackNotificationPort {

    private final RestClient restClient;
    private final String webhookUrl;

    public SlackAdapter(RestClient.Builder restClientBuilder, String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.restClient = restClientBuilder
            .baseUrl(webhookUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @Override
    public void send(String messageBody) {
        // Request DTO for Slack Incoming Webhook
        record SlackRequest(String text) {}

        try {
            restClient.post()
                .body(new SlackRequest(messageBody))
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            throw new SlackNotificationException("Failed to send Slack notification", e);
        }
    }
}
