package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without calling the real API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> postedMessages = new ArrayList<>();
    private boolean shouldSucceed = true;
    private String lastChannelId;

    @Override
    public boolean postMessage(String channelId, String messageBody) {
        this.lastChannelId = channelId;
        this.postedMessages.add(messageBody);
        // Simulate API behavior
        return shouldSucceed;
    }

    public List<String> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public String getLastMessageBody() {
        return postedMessages.isEmpty() ? null : postedMessages.get(postedMessages.size() - 1);
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

    public void reset() {
        postedMessages.clear();
        lastChannelId = null;
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }
}
