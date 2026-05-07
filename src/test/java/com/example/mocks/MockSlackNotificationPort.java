package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation for SlackNotificationPort.
 * Stores messages in memory to verify behavior without external I/O.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String body;

        public Message(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Simulate latency or network behavior if necessary, but for now just store.
        this.sentMessages.add(new Message(channel, messageBody));
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public boolean hasReceivedMessageContaining(String text) {
        return sentMessages.stream().anyMatch(m -> m.body.contains(text));
    }
}
