package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack notifications.
 * In a production environment, this would use Slack Web API.
 * For this defect validation, it simulates the interaction or logs it.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channel, String body) {
        // Implementation of actual Slack API call would go here.
        // Example: WebClient.post()... 
        // For the defect fix (VW-454), the critical part is constructing the body in the Service layer.
        // This adapter acts as the implementation of the Port interface.
        log.info("SLACK [{}]: {}", channel, body);
    }
}
