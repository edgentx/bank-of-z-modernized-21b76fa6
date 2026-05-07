package com.example.mocks;

import com.example.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for the NotificationPort.
 * Captures messages in memory for verification in tests.
 */
public class MockNotificationPort implements NotificationPort {
    public final List<Message> messages = new ArrayList<>();

    @Override
    public void sendNotification(String targetChannel, String messageBody) {
        this.messages.add(new Message(targetChannel, messageBody));
    }

    public record Message(String targetChannel, String body) {}

    public void clear() {
        messages.clear();
    }
}
