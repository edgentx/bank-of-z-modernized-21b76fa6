package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Captures messages sent to Slack for verification.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    public record SlackMessage(String channel, String body) {}

    private final List<SlackMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        messages.add(new SlackMessage(channel, body));
    }

    public List<SlackMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}