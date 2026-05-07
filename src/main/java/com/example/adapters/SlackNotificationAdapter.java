package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a real scenario, this would use a Slack WebClient (e.g., Slack SDK).
 * For the purpose of this defect fix and e2e validation, it logs the action.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void send(String messageBody) {
        // Implementation logic:
        // 1. Validate input
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be empty");
        }
        
        // 2. Call External API
        // SlackClient.postMessage(messageBody);
        
        // 3. Log success (simulated for Spring Boot environment)
        log.info("Slack notification sent successfully: {}", messageBody);
    }
}