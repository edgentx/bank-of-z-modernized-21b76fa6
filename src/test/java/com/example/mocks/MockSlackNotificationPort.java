package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();

    public static record Message(String channel, String body) {}

    @Override
    public void sendMessage(String channel, String body) {
        System.out.println("[Mock Slack] Sending to " + channel + ": " + body);
        this.messages.add(new Message(channel, body));
    }

    public void clear() {
        messages.clear();
    }
}
