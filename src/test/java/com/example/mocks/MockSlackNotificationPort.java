package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without network calls.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();
    private boolean shouldFail = false;

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public boolean postMessage(String channel, String messageBody) {
        if (shouldFail) {
            return false;
        }
        messages.add(new Message(channel, messageBody));
        return true;
    }

    public boolean containsUrlInBody(String url) {
        return messages.stream().anyMatch(msg -> msg.body().contains(url));
    }

    public record Message(String channel, String body) {}
}
