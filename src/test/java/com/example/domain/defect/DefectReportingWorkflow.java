package com.example.domain.defect;

import com.example.domain.ports.GithubIssueTracker;
import com.example.domain.ports.SlackNotifier;

/**
 * Workflow responsible for reporting defects.
 * Orchestrates the creation of a GitHub issue and subsequent notification via Slack.
 * This class serves as the use case or service layer implementation.
 */
public class DefectReportingWorkflow {

    private final GithubIssueTracker githubIssueTracker;
    private final SlackNotifier slackNotifier;

    /**
     * Constructor for dependency injection.
     *
     * @param githubIssueTracker The port implementation for GitHub interactions.
     * @param slackNotifier The port implementation for Slack notifications.
     */
    public DefectReportingWorkflow(GithubIssueTracker githubIssueTracker, SlackNotifier slackNotifier) {
        this.githubIssueTracker = githubIssueTracker;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Reports a defect by creating an issue on GitHub and notifying Slack.
     *
     * @param title The title of the defect.
     * @param description The description of the defect.
     * @throws IllegalStateException if the GitHub URL is not returned or is empty.
     */
    public void reportDefect(String title, String description) {
        // Step 1: Create Issue in GitHub
        String githubUrl = githubIssueTracker.createIssue(title, description);

        // Step 2: Validate result
        if (githubUrl == null || githubUrl.isEmpty()) {
            throw new IllegalStateException("Failed to retrieve GitHub URL from Issue Tracker");
        }

        // Step 3: Notify Slack with the GitHub URL included
        // Constructing the message body according to VW-454 requirements
        String messageBody = "Defect Reported: " + title + "\nGitHub Issue: " + githubUrl;
        slackNotifier.sendNotification(messageBody);
    }
}
