package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for sending Slack notifications.
 * Implemented by infrastructure adapters.
 */
public interface SlackNotificationPort {
    void sendNotification(String messageBody);
}
