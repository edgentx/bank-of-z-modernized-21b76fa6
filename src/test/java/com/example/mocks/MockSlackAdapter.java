package com.example.mocks;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Stores messages in memory to prevent external I/O.
 */
@Component
public class MockSlackAdapter implements SlackPort {

    private final List<String> messages = new ArrayList<>();
    private String mockBody;

    @Override
    public String getLastMessageBody() {
        if (messages.isEmpty()) return mockBody != null ? mockBody : "";
        return messages.get(messages.size() - 1);
    }

    @Override
    public void postMessage(String text) {
        messages.add(text);
    }

    /**
     * Helper to set a specific body for testing lookups
     */
    public void setMockBody(String body) {
        this.mockBody = body;
    }

    public void clear() {
        messages.clear();
        mockBody = null;
    }
}