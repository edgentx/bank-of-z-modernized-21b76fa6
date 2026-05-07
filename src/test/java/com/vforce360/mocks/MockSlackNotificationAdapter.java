package com.vforce360.mocks;

import com.vforce360.ports.slack.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures sent messages to verify content without calling the real API.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private String lastChannelId;
    private boolean shouldFail = false;

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        this.lastChannelId = channelId;
        this.sentMessages.add(messageBody);
        return !shouldFail;
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public String getLastMessageBody() {
        return sentMessages.isEmpty() ? null : sentMessages.get(sentMessages.size() - 1);
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public void reset() {
        sentMessages.clear();
        lastChannelId = null;
        shouldFail = false;
    }
}
