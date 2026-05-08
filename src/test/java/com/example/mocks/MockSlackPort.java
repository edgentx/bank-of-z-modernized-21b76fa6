package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to Slack to verify content.
 */
public class MockSlackPort implements SlackPort {
    
    private final List<String> postedMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean postMessage(String text) {
        if (shouldFail) return false;
        postedMessages.add(text);
        return true;
    }

    /**
     * Retrieves the last message sent to Slack.
     */
    public String getLastMessage() {
        if (postedMessages.isEmpty()) return null;
        return postedMessages.get(postedMessages.size() - 1);
    }

    /**
     * Utility for the test to verify the link is present.
     */
    public boolean lastMessageContains(String substr) {
        String last = getLastMessage();
        return last != null && last.contains(substr);
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
