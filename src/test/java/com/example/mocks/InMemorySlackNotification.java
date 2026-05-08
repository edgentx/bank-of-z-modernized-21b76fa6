package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify they contain the expected data.
 */
public class InMemorySlackNotification implements SlackNotificationPort {

    public static class PostedMessage {
        public final String channel;
        public final String body;

        public PostedMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        this.messages.add(new PostedMessage(channel, body));
    }

    public List<PostedMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    public boolean containsUrl(String url) {
        return messages.stream()
                .anyMatch(msg -> msg.body.contains(url));
    }
}
