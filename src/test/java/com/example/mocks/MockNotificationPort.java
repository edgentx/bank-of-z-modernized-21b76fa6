package com.example.mocks;

import com.example.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the NotificationPort for testing.
 * Captures calls to Slack and GitHub to verify behavior without side effects.
 */
public class MockNotificationPort implements NotificationPort {

    public final List<SlackCall> slackCalls = new ArrayList<>();
    public final List<GitHubCall> githubCalls = new ArrayList<>();

    public String githubUrlToReturn = "https://github.com/test-repo/issues/42";
    public boolean shouldFailSlack = false;

    @Override
    public boolean postToSlack(String channel, String message) {
        slackCalls.add(new SlackCall(channel, message));
        return !shouldFailSlack;
    }

    @Override
    public String createGitHubIssue(String title, String body) {
        githubCalls.add(new GitHubCall(title, body));
        return githubUrlToReturn;
    }

    public void reset() {
        slackCalls.clear();
        githubCalls.clear();
        githubUrlToReturn = "https://github.com/test-repo/issues/42";
        shouldFailSlack = false;
    }

    public record SlackCall(String channel, String message) {}
    public record GitHubCall(String title, String body) {}
}
