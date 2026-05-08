package com.example.ports;

import com.example.domain.notification.model.NotificationAggregate;

/**
 * Port interface for sending notifications.
 * Implementations will handle the specifics of Slack, Email, etc.
 */
public interface NotificationPort {
    void send(NotificationAggregate notification);
}
