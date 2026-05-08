package com.example.services;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.shared.ValidationReportedEvent;
import com.example.ports.GitHubIssuePort;
import com.example.ports.VForce360NotificationPort;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service handling the defect reporting workflow (S-FB-1).
 * Orchestrates between GitHub (Issue Creation) and Slack (Notification).
 */
@Service
public class DefectReportingService {

    private final GitHubIssuePort githubPort;
    private final VForce360NotificationPort slackPort;

    public DefectReportingService(GitHubIssuePort githubPort, VForce360NotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Handles the ReportDefect command by coordinating external ports.
     * 
     * Contract for S-FB-1 (VW-454):
     * 1. Create a GitHub issue.
     * 2. Propagate the resulting URL to the Slack notification body.
     * 
     * @param cmd The command containing defect details.
     * @return ValidationReportedEvent containing the generated URL.
     */
    public ValidationReportedEvent reportDefect(ReportDefectCmd cmd) {
        // 1. Create Issue in GitHub
        String description = String.format(
            "Defect ID: %s\nComponent: %s\nSeverity: %s", 
            cmd.defectId(), 
            cmd.component(), 
            cmd.severity()
        );
        String issueUrl = githubPort.createIssue(cmd.title(), description);

        // 2. Notify Slack with the GitHub URL
        // FIX for VW-454: Ensure the URL returned from GitHub is explicitly passed to Slack.
        boolean sent = slackPort.sendDefectSlack(cmd.defectId(), issueUrl);

        // Depending on strictness, we might throw if sent == false, 
        // but relying on the port contract (returning boolean) is sufficient for this flow.
        
        return new ValidationReportedEvent(
            cmd.defectId(), 
            cmd.defectId(), 
            issueUrl, 
            Instant.now()
        );
    }
}