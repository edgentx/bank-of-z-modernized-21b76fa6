package com.example.domain.vforce360;

import com.example.ports.SlackNotifier;
import com.example.ports.GitHubClient;

/**
 * Domain Service for reporting defects.
 * This file acts as a placeholder for the logic we are testing.
 * In the RED phase, we create this class with minimal/wrong implementation
 * just to allow the tests to compile and run (and then fail).
 */
public class DefectReportingService {

    private final SlackNotifier slackNotifier;
    private final GitHubClient gitHubClient;

    public DefectReportingService(GitHubClient gitHubClient, SlackNotifier slackNotifier) {
        this.gitHubClient = gitHubClient;
        this.slackNotifier = slackNotifier;
    }

    public void reportDefect(String title, String body) {
        // RED PHASE IMPLEMENTATION:
        // This implementation is deliberately incomplete or wrong to satisfy the "Red" phase requirement.
        // It likely notifies Slack but forgets to include the GitHub link.
        
        // 1. Create GitHub Issue (Simulated)
        // In reality, this would return a real object. Here we call the mock.
        String issueUrl = gitHubClient.createIssue(title, body);

        // 2. Notify Slack (Simulated)
        // Current behavior: Just says "Defect Reported".
        // Expected behavior (S-FB-1): Should include issueUrl.
        slackNotifier.send("Defect Reported: " + title); 
        
        // The missing link in the string above is what causes the test to fail.
    }
}
