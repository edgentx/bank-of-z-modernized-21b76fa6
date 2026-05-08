package com.example.validation.mocks;

import com.example.validation.domain.model.GitHubIssueLink;
import com.example.validation.ports.SlackPort;

public class MockSlackPort implements SlackPort {
    private boolean sendNotificationCalled = false;
    private GitHubIssueLink lastReceivedLink;

    @Override
    public void sendNotification(GitHubIssueLink link) {
        this.sendNotificationCalled = true;
        this.lastReceivedLink = link;
    }

    // Verification helpers
    public boolean wasSendNotificationCalled() {
        return sendNotificationCalled;
    }

    public GitHubIssueLink getLastReceivedLink() {
        return lastReceivedLink;
    }

    /**
     * Validates S-FB-1: Verifies the Slack body (simulated by the received link object) 
     * contains the GitHub issue URL.
     */
    public boolean doesBodyContainUrl() {
        if (lastReceivedLink == null) return false;
        String url = lastReceivedLink.url();
        return url != null && !url.isBlank() && url.startsWith("http");
    }
}
