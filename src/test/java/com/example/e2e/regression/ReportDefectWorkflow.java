package com.example.e2e.regression;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * System Under Test (SUT) wrapper for the Defect Reporting Logic.
 * This class encapsulates the workflow required to fix VW-454.
 * 
 * The defect was that the Slack notification was missing the GitHub URL.
 * The implementation now ensures that if a GitHub URL is generated,
 * it is strictly included in the Slack payload.
 */
public class ReportDefectWorkflow {
    
    private final SlackPort slack;
    private final GitHubPort github;

    public ReportDefectWorkflow(SlackPort slack, GitHubPort github) {
        this.slack = slack;
        this.github = github;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Creates an issue in GitHub.
     * 2. Posts a notification to Slack containing the GitHub URL.
     * 
     * @param title The title of the defect/issue
     * @param description The description of the defect
     * @param channelId The Slack channel ID to notify
     * @throws IllegalStateException if GitHub fails to return a valid URL
     */
    public void execute(String title, String description, String channelId) {
        // 1. Create GitHub Issue
        String url = github.createIssue(title, description);

        // 2. Validate State
        if (url == null) {
            // Fail fast as per test requirements
            throw new IllegalStateException("GitHub URL is null");
        }

        // 3. Construct Message (Fix for VW-454: Ensure URL is present)
        String message = "Defect Reported: " + title + "\nGitHub Issue: " + url;

        // 4. Post to Slack
        slack.postMessage(channelId, message);
    }
}
