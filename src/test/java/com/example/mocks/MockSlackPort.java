package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to Slack to verify content.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String text) {
        System.out.println("[MockSlack] Captured message: " + text);
        sentMessages.add(text);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean wasUrlSent(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }

    public void clear() {
        sentMessages.clear();
    }
}
