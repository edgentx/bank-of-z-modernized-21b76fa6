package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock for Slack notifications.
 * Allows tests to verify that a message was sent and inspect its content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<SlackMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        this.messages.add(new SlackMessage(channel, messageBody));
    }

    public void clear() {
        messages.clear();
    }

    public record SlackMessage(String channel, String body) {}
}
