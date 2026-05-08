package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier for testing purposes.
 * Records messages sent to allow verification in tests.
 */
public class MockSlackNotifier implements SlackNotifier {
    
    public static class Message {
        public final String channel;
        public final String message;

        public Message(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public void send(String channel, String message) {
        this.sentMessages.add(new Message(channel, message));
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}
