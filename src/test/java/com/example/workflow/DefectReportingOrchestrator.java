package com.example.workflow;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Orchestrator (Service/Activity) handling the defect reporting workflow.
 * 
 * This class is the place where the fix for VW-454 must be implemented.
 * It coordinates creating the GitHub issue and then notifying Slack with the URL.
 * 
 * NOTE: This file is provided as a placeholder to satisfy the compiler during the
 * Red Phase setup. In a real TDD loop, this would be written after the test fails.
 * Here, it represents the class under test.
 */
@Component
public class DefectReportingOrchestrator {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingOrchestrator(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void execute(ReportDefectCmd cmd) {
        // Implementation logic goes here.
        // 1. Create GitHub Issue
        // 2. Get URL
        // 3. Publish to Slack with URL in body
        
        // For the Red phase, this can be empty or throw NotImplementedException,
        // but we return a Mock implementation structure to allow the Test to compile
        // and fail on the *content* assertion.
        throw new UnsupportedOperationException("Implement VW-454 fix here");
    }
}
