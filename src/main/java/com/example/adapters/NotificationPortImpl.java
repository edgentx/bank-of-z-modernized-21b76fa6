package com.example.adapters;

import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for Notification Port (Slack).
 * Sends messages to a Slack webhook.
 */
public class NotificationPortImpl implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(NotificationPortImpl.class);

    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public NotificationPortImpl(String webhookUrl, RestTemplate restTemplate) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotification(String subject, String body) {
        log.info("Sending Slack notification: {}", subject);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> payload = new HashMap<>();
            payload.put("text", subject + "\n" + body);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
            
            log.debug("Slack notification sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            // Depending on requirements, we might swallow this or throw.
            // For now, we log and continue to prevent workflow rollback if Slack is down.
        }
    }
}
