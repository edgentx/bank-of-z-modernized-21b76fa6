package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to allow assertions on content.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> postedBodies = new ArrayList<>();

    @Override
    public void postMessage(String body) {
        postedBodies.add(body);
    }

    /**
     * Asserts that at least one message posted contains the specified text.
     */
    public boolean verifyBodyContains(String text) {
        return postedBodies.stream().anyMatch(body -> body.contains(text));
    }

    /**
     * Retrieves the most recently posted body.
     */
    public String getLastBody() {
        if (postedBodies.isEmpty()) return null;
        return postedBodies.get(postedBodies.size() - 1);
    }

    public void clear() {
        postedBodies.clear();
    }
}
