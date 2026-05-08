package com.example.adapters;

import java.util.ArrayList;
import java.util.List;

/**
 * Spy/Mock Adapter for Slack.
 * Captures messages sent to Slack for verification in tests.
 */
public class SpySlackNotifier implements SlackNotifierPort {

    public static final class Message {
        public final String channel;
        public final String message;

        public Message(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }

    private final List<Message> capturedMessages = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String message) {
        capturedMessages.add(new Message(channel, message));
    }

    public List<Message> getCapturedMessages() {
        return capturedMessages;
    }

    public boolean wasUrlPosted(String url) {
        return capturedMessages.stream()
                .anyMatch(msg -> msg.message.contains(url));
    }
}