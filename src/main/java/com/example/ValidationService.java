package com.example;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the validation and defect reporting logic.
 * Coordinates between GitHub (for issue creation) and Slack (for notifications).
 */
@Service
public class ValidationService {

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    public ValidationService(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * This method ensures the URL returned by GitHub is included in the Slack notification.
     *
     * @param title The title of the defect.
     * @param body  The description of the defect.
     */
    public void reportDefect(String title, String body) {
        // 1. Create the issue in GitHub
        String issueUrl = githubPort.createIssue(title, body);

        // 2. Compose the Slack message including the GitHub URL
        String message = "Issue created: " + issueUrl;

        // 3. Send the notification
        slackPort.sendMessage(message);
    }
}