package com.example.domain.vforce360.service;

import com.example.domain.shared.Command;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.domain.vforce360.ports.GitHubIssueTrackerPort;
import com.example.domain.vforce360.ports.VForce360NotifierPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service handling the logic for reporting defects.
 * Orchestrates the interaction between GitHub (recording) and Slack (notification).
 * 
 * This acts as the bridge between the Temporal workflow trigger and the domain ports.
 */
@Service
public class DefectReportingService {

    private final GitHubIssueTrackerPort gitHubTracker;
    private final VForce360NotifierPort notifier;

    /**
     * Constructor-based injection as per Spring Boot best practices.
     */
    public DefectReportingService(GitHubIssueTrackerPort gitHubTracker, VForce360NotifierPort notifier) {
        this.gitHubTracker = gitHubTracker;
        this.notifier = notifier;
    }

    /**
     * Executes the defect reporting logic.
     * 1. Creates the GitHub issue.
     * 2. Formats the Slack message (VW-454 Requirement: Must contain URL).
     * 3. Sends the notification.
     * 
     * @param cmd The command to execute.
     * @return The resulting domain event.
     */
    public DefectReportedEvent execute(Command cmd) {
        if (cmd instanceof ReportDefectCommand c) {
            // Step 1: Create GitHub Issue
            String issueUrl = gitHubTracker.createIssue(c.title(), c.description());

            // Step 2: Prepare Slack Body
            // CRITICAL: VW-454 Regression Test expects this exact format.
            String slackBody = String.format(
                "Defect Reported: %s\nGitHub Issue: %s",
                c.title(),
                issueUrl
            );

            // Step 3: Send Notification
            notifier.sendDefectReport(slackBody);

            // Step 4: Emit Domain Event
            return new DefectReportedEvent(c.defectId(), issueUrl, Instant.now());
        } 
        
        throw new IllegalArgumentException("Unsupported command type: " + cmd.getClass().getSimpleName());
    }
}
