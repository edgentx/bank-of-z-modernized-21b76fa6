package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * The System Under Test (SUT).
 * This class represents the workflow logic that would normally be inside
 * a Temporal Activity or a Spring Service.
 * 
 * In TDD Red phase, this class might not exist yet, or might be a stub.
 * We define it here to satisfy the compiler, but the assertions in the test
 * will fail because the implementation is likely empty or incorrect.
 */
public class DefectReportWorkflowService {

    private final GitHubPort githubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportWorkflowService(GitHubPort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect to VForce360.
     * 1. Creates GitHub Issue.
     * 2. Notifies Slack with the issue URL.
     */
    public void reportDefect(String title, String description) {
        // REAL IMPLEMENTATION GOES HERE
        // Currently left blank or returning default to ensure TEST FAILURE (Red Phase).
        
        // Example of what we eventually need:
        // String url = githubPort.createIssue(title, description);
        // String slackBody = "New Defect Reported: " + url;
        // slackPort.sendMessage("#vforce360-issues", slackBody);
        
        throw new UnsupportedOperationException("Not implemented yet");
    }
}