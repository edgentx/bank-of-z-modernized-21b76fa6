package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Captures messages posted to verify content in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<CapturedMessage> messages = new ArrayList<>();

    @Override
    public boolean postMessage(String channel, String messageBody) {
        messages.add(new CapturedMessage(channel, messageBody));
        return true;
    }

    public record CapturedMessage(String channel, String body) {}
}
