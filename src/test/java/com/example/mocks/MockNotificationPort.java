package com.example.mocks;

import com.example.ports.NotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Captures messages to verify content (e.g., GitHub URLs).
 */
public class MockNotificationPort implements NotificationPort {

    public final List<Message> messages = new ArrayList<>();

    public record Message(String subject, String body) {}

    @Override
    public void sendNotification(String subject, String body) {
        messages.add(new Message(subject, body));
    }

    public boolean wasCalledWithGitHubUrl(String urlFragment) {
        return messages.stream()
                .anyMatch(msg -> msg.body().contains(urlFragment) && msg.body().startsWith("http"));
    }
}
