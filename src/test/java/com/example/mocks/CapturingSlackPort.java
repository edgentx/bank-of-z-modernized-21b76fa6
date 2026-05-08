package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack API.
 * Captures messages sent to Slack to verify their content in tests.
 */
public class CapturingSlackPort implements SlackPort {

    private boolean called = false;
    private String lastBody;
    private final List<String> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        this.called = true;
        this.lastBody = body;
        this.messages.add(body);
        // In a real implementation, this would make an HTTP call
        System.out.println("[MOCK SLACK] Posted to " + channel + ": " + body);
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastMessageBody() {
        return lastBody;
    }
}