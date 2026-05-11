package com.example.application;

import com.example.domain.defect.model.GitHubIssueUrl;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.ports.DefectServicePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the orchestration of defect reporting.
 * In the CQRS model, this acts as the command handler for ReportDefectCommand.
 * 
 * It generates the necessary GitHub link (or retrieves it) and dispatches
 * the notification to the configured Slack channel.
 */
@Service
public class DefectReportingService implements DefectServicePort {

    private final SlackNotificationPort slackPort;

    @Autowired
    public DefectReportingService(SlackNotificationPort slackPort) {
        this.slackPort = slackPort;
    }

    @Override
    public void reportDefect(ReportDefectCommand cmd) {
        // Determine the GitHub URL for this defect.
        // This logic ensures that every defect report creates a linkable artifact.
        GitHubIssueUrl issueUrl = GitHubIssueUrl.forDefect(cmd.defectId());

        // Construct the message body.
        // The format must match the regex expectation in the test: "GitHub Issue: <url>"
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("*Defect Report Received*\n");
        bodyBuilder.append("*").append(cmd.title()).append("*\n");
        bodyBuilder.append("ID: ").append(cmd.defectId()).append("\n");
        bodyBuilder.append("Severity: ").append(cmd.severity()).append("\n");
        bodyBuilder.append("Component: ").append(cmd.component()).append("\n");
        
        // Critical fix for S-FB-1: Ensure the link is present
        bodyBuilder.append("GitHub Issue: ").append(issueUrl.url()).append("\n");

        // Send the notification via the port
        // Channel is hardcoded based on VForce360 convention, but could be configurable
        slackPort.sendNotification("#vforce360-issues", bodyBuilder.toString());
    }
}
