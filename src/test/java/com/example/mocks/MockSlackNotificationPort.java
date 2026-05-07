package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Used in tests to capture messages without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String title;
        public final String body;

        public Message(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void sendAlert(String title, String body) {
        System.out.println("[MockSlack] Capturing alert: " + title);
        this.messages.add(new Message(title, body));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
