package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service implementation (placeholder to be implemented).
 * This class is required to compile the tests, but will be empty/throw exceptions
 * until the implementation phase (Green).
 */
public class ValidationService {

    private final SlackNotificationPort slackPort;
    private final GitHubIssuePort githubPort;

    public ValidationService(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    public void reportDefect(String id, String title, String description) {
        // TODO: Implement logic to create GitHub issue and post to Slack
        // This logic should satisfy the tests in SlackNotificationServiceTest
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
