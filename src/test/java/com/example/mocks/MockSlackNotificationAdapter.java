package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for Slack Notifications.
 * Used in testing to capture outbound messages without real I/O.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {
    private String lastChannel;
    private String lastBody;
    private boolean shouldFail = false;

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastBody() {
        return lastBody;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public boolean sendMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        return !shouldFail;
    }

    @Override
    public boolean sendRichMessage(String channel, String text, Map<String, Object> metadata) {
        // Simple mock implementation that concatenates text and metadata URL
        StringBuilder sb = new StringBuilder();
        sb.append(text).append("\n");
        if (metadata.containsKey("url")) {
            sb.append("GitHub issue: ").append(metadata.get("url"));
        }
        return sendMessage(channel, sb.toString());
    }

    public void clear() {
        this.lastChannel = null;
        this.lastBody = null;
    }
}