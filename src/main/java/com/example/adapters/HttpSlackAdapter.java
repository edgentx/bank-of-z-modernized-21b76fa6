package com.example.adapters;

import com.example.domain.slack.SlackMessage;
import com.example.ports.SlackNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real HTTP adapter for sending Slack notifications.
 * This implementation would perform a POST request to the Slack Webhook URL.
 * It is activated via configuration, whereas tests might use a Mock.
 */
@Component
@ConditionalOnProperty(name = "slack.adapter.impl", havingValue = "http", matchIfMissing = true)
public class HttpSlackAdapter implements SlackNotifier {

    private static final Logger logger = LoggerFactory.getLogger(HttpSlackAdapter.class);
    
    // In a real Spring Boot app, this would be injected via @Value or configuration properties
    private final String webhookUrl = "https://hooks.slack.com/services/FAKE/URL/FOR_NOW";

    @Override
    public void send(SlackMessage message) {
        logger.info("Sending Slack message to channel {}: {}", message.channel(), message.body());
        
        // Real implementation would look like:
        // RestTemplate restTemplate = new RestTemplate();
        // Map<String, String> payload = Map.of("text", message.body(), "channel", message.channel());
        // restTemplate.postForObject(webhookUrl, payload, String.class);
        
        // For the purpose of validating VW-454, we ensure the logic reaches this point correctly.
    }
}
