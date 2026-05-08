package com.example.e2e.regression;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * SUT Wrapper for the Defect Reporting Logic.
 * In a real Temporal environment, this would wrap the Activity/Workflow interface.
 * This class represents the missing implementation that causes the defect.
 * It is intentionally implemented incorrectly (or left empty) to verify the tests fail initially,
 * or provided as a stub if we are writing tests before code.
 * 
 * For this exercise, we provide a "broken" implementation to demonstrate the defect,
 * or a stub if this is purely pre-implementation.
 */
public class ReportDefectWorkflow {
    private final SlackPort slack;
    private final GitHubPort github;

    public ReportDefectWorkflow(SlackPort slack, GitHubPort github) {
        this.slack = slack;
        this.github = github;
    }

    public void execute(String title, String description, String channelId) {
        // DEFECT SIMULATION (Red Phase):
        // The code currently posts to Slack without the GitHub URL.
        // We want the test to CATCH this.
        
        // 1. Create GitHub Issue
        String url = github.createIssue(title, description);

        // 2. Post to Slack (The Bug: The URL is missing here)
        // String message = "Defect Reported: " + title; // BUGGY CODE (Tests will FAIL)
        
        // Correct Code (To make tests pass later):
        if (url == null) {
             throw new IllegalStateException("GitHub URL is null");
        }
        String message = "Defect Reported: " + title + "\nGitHub Issue: " + url;

        slack.postMessage(channelId, message);
    }
}
