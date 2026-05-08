package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort.
 * Used to capture messages sent to Slack for assertion.
 */
public class MockSlackPort implements SlackPort {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;
    public String lastBody;

    @Override
    public void sendMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        this.messages.add(body);
    }

    public boolean wasUrlSent(String url) {
        return lastBody != null && lastBody.contains(url);
    }

    public void clear() {
        messages.clear();
        lastChannel = null;
        lastBody = null;
    }
}
