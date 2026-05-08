package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to Slack to verify content.
 */
public class MockSlackPort implements SlackPort {

    public final List<String> sentMessages = new ArrayList<>();
    public String lastChannelId;

    @Override
    public void sendMessage(String channelId, String message) {
        this.lastChannelId = channelId;
        this.sentMessages.add(message);
        // Simulate no exception thrown
    }

    public void reset() {
        sentMessages.clear();
        lastChannelId = null;
    }
}
