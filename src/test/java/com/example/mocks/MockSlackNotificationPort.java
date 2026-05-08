package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

public class MockSlackNotificationPort implements SlackNotificationPort {
    public String lastBody;
    public Map<String, String> lastAttachments;
    public int invocationCount = 0;

    @Override
    public void sendNotification(String body, Map<String, String> attachments) {
        this.lastBody = body;
        this.lastAttachments = new HashMap<>(attachments); // Defensive copy
        this.invocationCount++;
    }

    public void reset() {
        this.lastBody = null;
        this.lastAttachments = null;
        this.invocationCount = 0;
    }

    /**
     * Helper assertion for testing.
     * Verifies that the 'github_url' key exists and is not blank in the attachments map.
     */
    public void assertGithubUrlPresent() {
        if (this.lastAttachments == null) {
            throw new AssertionError("Slack was not invoked");
        }
        String url = this.lastAttachments.get("github_url");
        if (url == null || url.isBlank()) {
            throw new AssertionError("Slack attachments missing 'github_url' or it is blank. Received: " + this.lastAttachments);
        }
    }
}
