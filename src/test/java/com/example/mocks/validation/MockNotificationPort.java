package com.example.mocks.validation;

import com.example.domain.validation.ports.NotificationPort;

/**
 * Mock adapter for Notifications (Slack).
 */
public class MockNotificationPort implements NotificationPort {
    private String lastPayload;

    public String getLastPayload() {
        return lastPayload;
    }

    @Override
    public void sendNotification(String payload) {
        this.lastPayload = payload;
    }
}
