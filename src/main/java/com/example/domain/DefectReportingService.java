package com.example.domain;

import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for reporting defects and coordinating between
 * GitHub issue creation and Slack notifications.
 * <p>
 * This class implements the logic required to satisfy the VW-454 validation:
 * When a defect is reported, a GitHub issue is created, and the resulting URL
 * must be formatted correctly within the Slack notification body.
 */
@Service
public class DefectReportingService {

    private final GitHubRepositoryPort githubRepo;
    private final SlackNotificationPort slackNotifier;

    /**
     * Constructor for Dependency Injection.
     * Uses constructor injection as per Spring Boot best practices.
     *
     * @param githubRepo      The port for interacting with GitHub.
     * @param slackNotifier   The port for sending Slack notifications.
     */
    public DefectReportingService(GitHubRepositoryPort githubRepo,
                                   SlackNotificationPort slackNotifier) {
        this.githubRepo = githubRepo;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Reports a defect by creating an issue on GitHub and notifying a Slack channel.
     * <p>
     * Process:
     * 1. Create issue via GitHub port.
     * 2. Format the message body containing the GitHub URL.
     * 3. Send the notification via Slack port.
     *
     * @param title   The title of the defect/issue.
     * @param body    The description body of the defect.
     * @param channel The target Slack channel (e.g., #vforce360-issues).
     */
    public void reportDefect(String title, String body, String channel) {
        // Step 1: Create GitHub Issue
        String issueUrl = githubRepo.createIssue(title, body);

        // Step 2: Format Slack message to include GitHub issue URL
        // Validating VW-454: Body must contain 'GitHub issue: <url>'
        String slackMessageBody = String.format("GitHub issue: %s", issueUrl);

        // Step 3: Send Notification
        slackNotifier.sendMessage(channel, slackMessageBody);
    }
}
