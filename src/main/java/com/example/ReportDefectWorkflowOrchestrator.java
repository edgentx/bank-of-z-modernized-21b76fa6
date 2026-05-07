package com.example;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotifierPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Orchestrator for the "Report Defect" Temporal workflow logic.
 * In a real Spring Boot Temporal app, this would be a Workflow Implementation.
 * For unit testing the defect fix, we treat it as a service collaborating with ports.
 */
@Service
public class ReportDefectWorkflowOrchestrator {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotifierPort slackNotifierPort;

    @Autowired
    public ReportDefectWorkflowOrchestrator(GitHubIssuePort gitHubIssuePort, SlackNotifierPort slackNotifierPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotifierPort = slackNotifierPort;
    }

    public void reportDefect(String defectId, String summary, String description) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubIssuePort.createIssue(summary, description);

        // 2. Notify Slack
        // The defect (VW-454) implies the issueUrl was missing or malformed here.
        String slackBody = "Defect Reported: " + summary + "\nIssue: " + issueUrl;
        slackNotifierPort.sendNotification(slackBody);
    }
}
