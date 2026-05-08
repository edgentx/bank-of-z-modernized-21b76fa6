package com.example.mocks;

import com.example.ports.SlackWebhookPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackWebhookPort for testing.
 * Captures the body sent to Slack to verify content.
 */
public class MockSlackClient implements SlackWebhookPort {

    private final List<String> payloads = new ArrayList<>();

    @Override
    public void send(String body) {
        this.payloads.add(body);
    }

    public String getLastPayload() {
        if (payloads.isEmpty()) return null;
        return payloads.get(payloads.size() - 1);
    }

    public boolean wasCalled() {
        return !payloads.isEmpty();
    }
}