package com.example.domain.defect;

import com.example.domain.ports.GithubIssueTracker;
import com.example.domain.ports.SlackNotifier;

/**
 * Wrapper for the SUT (System Under Test).
 * Represents the Temporal Workflow logic orchestrating the reporting.
 * In a real test, this would be the Workflow class.
 */
public class DefectReportingWorkflow {

    private final GithubIssueTracker github;
    private final SlackNotifier slack;

    public DefectReportingWorkflow(GithubIssueTracker github, SlackNotifier slack) {
        this.github = github;
        this.slack = slack;
    }

    public void reportDefect(String title, String description) {
        // Step 1: Create Issue in GitHub
        String githubUrl = github.createIssue(title, description);

        if (githubUrl == null || githubUrl.isEmpty()) {
            throw new IllegalStateException("Failed to retrieve GitHub URL from Issue Tracker");
        }

        // Step 2: Notify Slack with the GitHub URL included
        // This is the specific logic path failing in VW-454
        String messageBody = "Defect Reported: " + title + "\nGitHub Issue: " + githubUrl;
        slack.sendNotification(messageBody);
    }
}
