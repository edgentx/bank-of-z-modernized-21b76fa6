package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Stores messages in memory for test verification.
 */
@Component
public class InMemorySlackNotificationAdapter implements SlackNotificationPort {

    private final List<Message> messages = new ArrayList<>();

    public record Message(String channelId, String body) {}

    @Override
    public void postMessage(String channelId, String messageBody) {
        messages.add(new Message(channelId, messageBody));
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1).body();
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
