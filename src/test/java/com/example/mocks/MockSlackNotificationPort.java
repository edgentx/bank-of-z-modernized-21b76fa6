package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory to verify contents without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> postedMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean postMessage(String body) {
        if (shouldFail) {
            return false;
        }
        postedMessages.add(body);
        return true;
    }

    public List<String> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void clear() {
        postedMessages.clear();
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
