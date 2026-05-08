package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures posted messages in a thread-safe queue for verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final BlockingQueue<String> postedBodies = new ArrayBlockingQueue<>(100);
    private boolean shouldSucceed = true;

    @Override
    public boolean postMessage(String body, Map<String, String> metadata) {
        // In a real scenario, we might want to capture metadata too, but the defect focuses on the body content.
        postedBodies.offer(body);
        return shouldSucceed;
    }

    /**
     * Retrieves the last message sent to Slack (blocking with timeout).
     */
    public String getLastMessage() throws InterruptedException {
        return postedBodies.take(); // Throws exception if empty (good for failing tests)
    }

    /**
     * Retrieves the last message sent to Slack (non-blocking).
     */
    public String peekLastMessage() {
        return postedBodies.peek();
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }

    public void clear() {
        postedBodies.clear();
    }
}
