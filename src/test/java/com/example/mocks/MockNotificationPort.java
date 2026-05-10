package com.example.mocks;

import com.example.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Captures sent messages for assertion.
 */
public class MockNotificationPort implements NotificationPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendNotification(String body) {
        // In a real test, we might verify format here, but usually, we just capture.
        this.messages.add(body);
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
