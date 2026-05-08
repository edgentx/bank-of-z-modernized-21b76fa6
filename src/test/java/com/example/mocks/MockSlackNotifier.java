package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier for testing.
 * Captures payloads sent to Slack so assertions can be made against them.
 */
public class MockSlackNotifier implements SlackNotifier {

    public final List<SlackMessagePayload> receivedPayloads = new ArrayList<>();

    @Override
    public void notify(SlackMessagePayload payload) {
        // Store the payload in memory instead of making an HTTP call
        receivedPayloads.add(payload);
    }

    public void clear() {
        receivedPayloads.clear();
    }
}
