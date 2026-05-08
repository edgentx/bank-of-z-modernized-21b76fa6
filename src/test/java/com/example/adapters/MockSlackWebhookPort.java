package com.example.adapters;

import com.example.ports.SlackWebhookPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackWebhookPort for testing.
 * Captures payloads to verify they contain the required links.
 */
public class MockSlackWebhookPort implements SlackWebhookPort {

    private final List<String> capturedPayloads = new ArrayList<>();

    @Override
    public void send(String jsonPayload) {
        capturedPayloads.add(jsonPayload);
    }

    public List<String> getCapturedPayloads() {
        return capturedPayloads;
    }

    public void reset() {
        capturedPayloads.clear();
    }
}
