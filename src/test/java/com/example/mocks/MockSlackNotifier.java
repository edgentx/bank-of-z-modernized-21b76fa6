package com.example.mocks;

import com.example.ports.SlackNotifierPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifierPort for testing.
 * Captures messages sent to Slack to verify their content in tests.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean notify(String channelId, String message) {
        if (shouldFail) {
            return false;
        }
        sentMessages.add(message);
        return true;
    }

    /**
     * Retrieves the last message sent to Slack.
     */
    public String getLastMessage() {
        if (sentMessages.isEmpty()) {
            return null;
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    /**
     * Checks if the Slack body contains a specific string (e.g., the GitHub URL).
     */
    public boolean bodyContains(String text) {
        return sentMessages.stream().anyMatch(msg -> msg != null && msg.contains(text));
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public void clear() {
        sentMessages.clear();
    }
}
