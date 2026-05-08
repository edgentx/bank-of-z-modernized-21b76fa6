package mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages for verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String body;

        public Message(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        System.out.println("[MockSlack] Capturing message to " + channel + ": " + body);
        this.messages.add(new Message(channel, body));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) {
            throw new IllegalStateException("No messages captured");
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
