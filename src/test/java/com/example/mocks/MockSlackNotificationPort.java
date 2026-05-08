package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify their content without
 * making actual network calls.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        // In a real mock, we might track specific calls, but capturing the body
        // is sufficient for verifying defect reports.
        this.sentMessages.add(messageBody);
    }

    @Override
    public String getLastMessageBody() {
        if (sentMessages.isEmpty()) {
            return null;
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    /**
     * Helper method for tests to verify if the Slack body contains the GitHub URL.
     * Directly implements the AC: "Slack body includes GitHub issue: <url>"
     *
     * @param url The expected GitHub URL.
     * @return true if the URL is found in the last message body.
     */
    public boolean doesLastMessageContainUrl(String url) {
        String body = getLastMessageBody();
        return body != null && body.contains(url);
    }

    public void clear() {
        sentMessages.clear();
    }
}
