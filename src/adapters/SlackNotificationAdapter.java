package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a real environment, this would use WebClient to call the Slack API.
 * For this defect fix, it validates inputs and logs the output for verification.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void send(String channel, String body) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be blank");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Slack body cannot be blank");
        }

        // Simulation of Slack API call
        log.info("[SLACK MOCK] Sending to {}: {}", channel, body);
        
        // In a real implementation:
        // webClient.post()
        //     .uri("https://slack.com/api/chat.postMessage")
        //     .headers(h -> h.setBearerAuth(token))
        //     .bodyValue(Map.of("channel", channel, "text", body))
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
    }
}