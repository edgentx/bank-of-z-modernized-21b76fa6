package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify their content in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<SlackMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String body) {
        this.messages.add(new SlackMessage(channel, body));
    }

    public void clear() {
        messages.clear();
    }

    public record SlackMessage(String channel, String body) {}
}
