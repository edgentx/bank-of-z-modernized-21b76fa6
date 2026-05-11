package com.example.mocks;

import com.example.ports.ExternalDefectSystemPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for external systems (GitHub/Slack).
 * Captures outputs for verification in tests without making actual network calls.
 */
public class MockExternalDefectSystemAdapter implements ExternalDefectSystemPort {

    private String mockGitHubUrlBase = "https://github.com/bank-of-z/issues/";
    private int issueCounter = 100;

    // Captured interactions for assertions
    public final List<String> slackMessagesSent = new ArrayList<>();
    public final List<String> githubTitlesCreated = new ArrayList<>();

    @Override
    public String createGitHubIssue(String title, String description) {
        this.githubTitlesCreated.add(title);
        // Simulate returning a valid URL format
        return mockGitHubUrlBase + issueCounter++;
    }

    @Override
    public void sendSlackNotification(String message) {
        this.slackMessagesSent.add(message);
    }

    /**
     * Helper to check if the Slack message contains the specific GitHub URL.
     * Addresses AC: "Slack body includes GitHub issue: <url>"
     */
    public boolean wasUrlSentToSlack(String expectedUrl) {
        return slackMessagesSent.stream()
                .anyMatch(msg -> msg.contains(expectedUrl));
    }

    public void reset() {
        slackMessagesSent.clear();
        githubTitlesCreated.clear();
    }
}
