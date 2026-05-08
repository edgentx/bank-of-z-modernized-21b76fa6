package com.example.domain.vforce360.service;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;

/**
 * Workflow implementation for defect reporting.
 * Orchestrates GitHub Issue creation and Slack notification.
 */
public class VForce360Workflow {

    private final GitHubPort gitHubPort;
    private final SlackNotifierPort slackNotifierPort;

    public VForce360Workflow(GitHubPort gitHubPort, SlackNotifierPort slackNotifierPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotifierPort = slackNotifierPort;
    }

    /**
     * Initiates the defect report process.
     * GREEN PHASE: Implementation logic added.
     */
    public String initiateDefectReport(String defectId, String description) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(defectId, description);
        
        // 2. Compose Slack Body with URL
        String body = "Defect reported: " + defectId + "\nGitHub Issue: " + issueUrl;
        
        // 3. Send Slack Notification
        slackNotifierPort.sendNotification("#vforce360-issues", body);
        
        return defectId;
    }
}
