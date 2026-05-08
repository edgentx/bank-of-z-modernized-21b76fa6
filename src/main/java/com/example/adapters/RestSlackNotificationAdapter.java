package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Real-world implementation of SlackNotificationPort using Spring RestTemplate.
 * Posts messages to the Slack Webhook API.
 */
@Component
public class RestSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(RestSlackNotificationAdapter.class);

    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public RestSlackNotificationAdapter(RestTemplate restTemplate,
                                        @Value("${slack.webhook.url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public boolean postMessage(String body, Map<String, String> metadata) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack webhook URL is not configured. Message not sent.");
            return false;
        }

        try {
            // Construct standard Slack JSON payload
            Map<String, Object> payload = Map.of(
                "text", body,
                "blocks", List.of(
                    Map.of(
                        "type", "section",
                        "text", Map.of(
                            "type", "mrkdwn",
                            "text", body
                        )
                    )
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            // Synchronous call. Consider Async for production high-throughput.
            restTemplate.postForEntity(webhookUrl, request, String.class);
            
            log.info("Successfully posted message to Slack for metadata: {}", metadata);
            return true;
        } catch (Exception e) {
            log.error("Failed to post message to Slack", e);
            return false;
        }
    }
}
