package com.example.mocks;

import com.example.ports.SlackClient;
import java.util.Map;
import java.util.HashMap;

/**
 * Mock implementation of SlackClient for testing.
 * Allows capture of the payload sent by the application for assertion.
 */
public class MockSlackClient implements SlackClient {

    private Map<String, Object> lastCapturedPayload;

    @Override
    public void sendMessage(Map<String, Object> payload) {
        // Instead of making a real HTTP call, we capture the data
        this.lastCapturedPayload = payload;
    }

    /**
     * Retrieves the last payload sent to this mock.
     * Used in tests to verify content.
     */
    public Map<String, Object> getCapturedPayload() {
        return lastCapturedPayload;
    }
}
