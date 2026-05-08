package com.example.adapters;

import com.example.ports.VForce360NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real adapter for VForce360 notifications.
 * Connects to Slack via HTTP API (simulated).
 */
@Component
@ConditionalOnProperty(name = "app.slack.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotificationAdapter implements VForce360NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void publishDefect(String title, String description, String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub URL cannot be null or blank");
        }

        // Implementation logic to send data to Slack
        // In a real scenario, this would use Slack WebClient or RestTemplate
        log.info("Publishing to Slack: {} | {} | GitHub: {}", title, description, githubUrl);
        
        // Simulate successful transmission
        doSend(title, description, githubUrl);
    }

    private void doSend(String title, String description, String githubUrl) {
        // Actual Slack API call logic would go here
        // e.g. webClient.post()...
    }
}
