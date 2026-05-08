package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort.
 * Captures messages sent to Slack for verification in tests.
 */
public class MockSlackAdapter implements SlackPort {

    public final List<String> sentMessages = new ArrayList<>();
    public String lastChannelId;
    private boolean shouldFail = false;

    public void reset() {
        sentMessages.clear();
        lastChannelId = null;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public boolean sendMessage(String channelId, List<String> messageBlocks) {
        lastChannelId = channelId;
        // Join blocks for easier verification
        sentMessages.add(String.join("\n", messageBlocks));
        return !shouldFail;
    }

    public boolean lastMessageContains(String text) {
        if (sentMessages.isEmpty()) return false;
        return sentMessages.get(sentMessages.size() - 1).contains(text);
    }
}