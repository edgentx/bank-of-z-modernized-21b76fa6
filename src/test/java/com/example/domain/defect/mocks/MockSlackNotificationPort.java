package com.example.domain.defect.mocks;

import com.example.domain.defect.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing purposes.
 * Captures the output to verify content without performing real network I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastBody;

    @Override
    public void send(String body) {
        this.lastBody = body;
    }

    /**
     * Helper method for assertions to retrieve the last captured body.
     *
     * @return The last string sent to the send method.
     */
    public String getLastBody() {
        return lastBody;
    }
}
