package com.example.mocks;

import com.example.application.SlackService;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackService for testing.
 * Captures messages sent to Slack for verification.
 */
public class MockSlackService implements SlackService {
    public final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        messages.add(message);
    }

    public boolean containsUrl(String url) {
        return messages.stream().anyMatch(msg -> msg.contains(url));
    }

    public void clear() {
        messages.clear();
    }
}