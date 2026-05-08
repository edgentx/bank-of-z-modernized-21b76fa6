package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a production environment, this would use the Slack Web API to post a message.
 * Currently, it logs the message to standard out to ensure the build passes integration
 * without requiring external API keys.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String messageBody) {
        // TODO: Replace with actual Slack WebClient call (e.g., SlackApi).
        // Blocking the implementation on S-FB-1 to pass validation logic tests first.
        log.info("[SLACK ADAPTER] Sending notification: {}", messageBody);
        System.out.println("[SLACK ADAPTER] Sending notification: " + messageBody);
    }
}
