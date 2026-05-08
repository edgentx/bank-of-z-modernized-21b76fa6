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
        if (shouldFail) {
            return false;
        }
        postedMessages.add(text);
        return true;
    }

    public List<String> getPostedMessages() {
        return postedMessages;
    }

    public boolean containsMessageWithSubstring(String substring) {
        return postedMessages.stream().anyMatch(msg -> msg.contains(substring));
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
