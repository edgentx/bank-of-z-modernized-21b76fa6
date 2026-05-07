package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Records sent messages to allow verification of content, specifically URLs.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean wasUrlSent(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }

    public void reset() {
        sentMessages.clear();
    }
}
