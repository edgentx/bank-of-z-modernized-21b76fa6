package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real-world adapter for Slack notifications.
 * Currently acts as a stub that logs to stdout or a logging framework.
 * In a full implementation, this would use the Slack WebAPI client.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void send(String messageBody) {
        if (messageBody == null) {
            throw new IllegalArgumentException("messageBody cannot be null");
        }
        // Stub implementation: Log instead of making HTTP call
        log.info("[SLACK] Sending message: {}", messageBody);
        System.out.println("[SLACK] " + messageBody);
    }
}
