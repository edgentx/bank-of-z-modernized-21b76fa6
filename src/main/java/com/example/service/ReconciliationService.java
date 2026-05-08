package com.example.service;

import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service handling Reconciliation logic.
 * Acts as the bridge between Domain Commands and Infrastructure Ports (e.g. Slack).
 */
@Service
public class ReconciliationService {

    private final SlackNotificationPort slackNotificationPort;

    public ReconciliationService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefect command.
     * This method orchestrates the logic to notify external systems.
     * 
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCmd cannot be null");
        }
        if (cmd.githubIssueUrl() == null || cmd.githubIssueUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub Issue URL is required");
        }

        String channel = "#vforce360-issues";
        
        // Construct the message body
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Reported:\n");
        bodyBuilder.append("Batch ID: ").append(cmd.batchId()).append("\n");
        bodyBuilder.append("Reason: ").append(cmd.reason()).append("\n");
        bodyBuilder.append("GitHub Issue: ").append(cmd.githubIssueUrl()).append("\n");

        slackNotificationPort.sendNotification(channel, bodyBuilder.toString());
    }
}