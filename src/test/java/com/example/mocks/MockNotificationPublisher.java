package com.example.mocks;

import com.example.ports.NotificationPublisher;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPublisher for testing.
 * Captures published messages for assertion.
 */
public class MockNotificationPublisher implements NotificationPublisher {
    public final List<PublishedMessage> messages = new ArrayList<>();

    @Override
    public void publish(String topic, String message) {
        messages.add(new PublishedMessage(topic, message));
    }

    public void reset() {
        messages.clear();
    }

    public record PublishedMessage(String topic, String content) {}
}