package com.example.domain.validation;

import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;
import org.springframework.stereotype.Service;

/**
 * Domain Service implementation for S-FB-1.
 * Orchestrates the reporting of a defect by creating a GitHub issue
 * and notifying Slack with the resulting URL.
 */
@Service
public class DefectReportService {

    private final GitHubIssueTracker gitHub;
    private final SlackNotifier slack;

    /**
     * Constructor injection for Adapters.
     */
    public DefectReportService(GitHubIssueTracker gitHub, SlackNotifier slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * Executes the defect reporting workflow.
     *
     * @param title The title of the defect.
     * @param body  The description body of the defect.
     * @param label The label to categorize the defect (e.g., 'bug').
     */
    public void reportDefect(String title, String body, String label) {
        // 1. Create the GitHub issue via the Adapter.
        // This returns the URL of the created issue.
        String issueUrl = gitHub.createIssue(title, body, label);

        // 2. Construct the Slack message.
        // FIX for VW-454: Explicitly include the issueUrl in the message format.
        String slackMessage = String.format("Defect Reported: %s. GitHub URL: %s", title, issueUrl);

        // 3. Send the notification via the Adapter.
        slack.sendNotification(slackMessage);
    }
}
