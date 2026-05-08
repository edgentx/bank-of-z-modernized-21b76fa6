package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to allow assertions on content.
 */
public class MockSlackPort implements SlackPort {
    private final List<String> postedMessages = new ArrayList<>();
    private String lastChannelId;

    @Override
    public void postMessage(String channelId, String text) {
        this.lastChannelId = channelId;
        this.postedMessages.add(text);
    }

    public String getLastMessageContent() {
        if (postedMessages.isEmpty()) return null;
        return postedMessages.get(postedMessages.size() - 1);
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

    public boolean messageContains(String substring) {
        String content = getLastMessageContent();
        return content != null && content.contains(substring);
    }

    public void reset() {
        postedMessages.clear();
        lastChannelId = null;
    }
}
