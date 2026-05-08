package com.example.mocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Used in tests to verify the body content without external calls.
 */
public class MockSlackNotifier {
    private final List<String> sentBodies = new ArrayList<>();

    public void send(String body) {
        sentBodies.add(body);
    }

    public String getLastBody() {
        if (sentBodies.isEmpty()) return null;
        return sentBodies.get(sentBodies.size() - 1);
    }

    public boolean containsUrl(String url) {
        return sentBodies.stream().anyMatch(body -> body.contains(url));
    }

    public void clear() {
        sentBodies.clear();
    }
}
