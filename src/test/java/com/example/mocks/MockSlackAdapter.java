package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock Slack Adapter for testing.
 * Captures messages sent to Slack to verify content (e.g. GitHub URLs) in tests.
 */
public class MockSlackAdapter implements SlackPort {

    public final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        sentMessages.add(messageBody);
    }

    public void reset() {
        sentMessages.clear();
    }

    public boolean containsUrl(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}
