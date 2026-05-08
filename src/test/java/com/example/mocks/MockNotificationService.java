package com.example.mocks;

import com.example.vforce.adapter.NotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of NotificationPort.
 * Captures the last sent payload for assertions.
 */
public class MockNotificationService implements NotificationPort {

    private Map<String, String> lastPayload;
    public boolean notificationSent = false;

    @Override
    public void sendNotification(Map<String, String> payload) {
        this.lastPayload = new HashMap<>(payload);
        this.notificationSent = true;
    }

    public Map<String, String> getLastPayload() {
        return lastPayload;
    }

    public String getMessageBody() {
        return lastPayload != null ? lastPayload.get("body") : null;
    }
}
