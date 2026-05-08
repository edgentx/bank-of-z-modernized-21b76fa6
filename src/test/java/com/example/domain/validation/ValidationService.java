package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service implementation for reporting defects.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
@Service
public class ValidationService {

    private final SlackNotificationPort slackPort;
    private final GitHubIssuePort githubPort;

    public ValidationService(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and posting a notification to Slack.
     *
     * @param id          The defect ID (e.g., "S-FB-1").
     * @param title       The title of the defect/issue.
     * @param description Additional description details.
     */
    public void reportDefect(String id, String title, String description) {
        // 1. Create the issue in GitHub
        String issueUrl = githubPort.createIssue(title, description);

        // 2. Construct the Slack message body
        // Requirement: The body must include the GitHub issue URL formatted as <url> for auto-linking.
        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Defect Reported: ").append(title).append("\n");
        messageBody.append("ID: ").append(id).append("\n");

        if (issueUrl != null && !issueUrl.isEmpty()) {
            // VW-454: Slack link formatting standard
            messageBody.append("GitHub Issue: <").append(issueUrl).append(">");
        } else {
            // Fallback if GitHub creation did not return a URL
            messageBody.append("GitHub Issue: Link generation pending or failed.");
        }

        // 3. Send the notification
        slackPort.sendMessage(messageBody.toString());
    }
}
