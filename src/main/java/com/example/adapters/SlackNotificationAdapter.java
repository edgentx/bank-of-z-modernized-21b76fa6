package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a production environment, this would use the Slack Web API client.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String body) {
        // Real implementation would go here (e.g., WebClient.post()...)
        // For now, we log to simulate the side-effect in a real environment.
        log.info("[SLACK_ADAPTER] Sending message: {}", body);
        
        // Pseudo-code for actual Slack call:
        // webClient.post()
        //     .uri("https://slack.com/api/chat.postMessage")
        //     .bodyValue(Map.of("text", body))
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
    }
}
