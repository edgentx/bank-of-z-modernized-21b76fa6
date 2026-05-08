package com.example.application;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the defect reporting logic.
 * This acts as the bridge between the Temporal Workflow/Activities and the Notification Port.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCmd.
     * Formats the message including the critical GitHub URL and dispatches it via the port.
     *
     * @param cmd The command containing defect details and the generated GitHub URL.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Defect Reported: ").append(cmd.title()).append("\n");
        messageBody.append("ID: ").append(cmd.defectId()).append("\n");
        messageBody.append("Severity: ").append(cmd.severity()).append("\n");
        messageBody.append("Description: ").append(cmd.description()).append("\n");
        
        // CRITICAL FIX for VW-454: Ensure the GitHub URL is appended to the message body
        messageBody.append("GitHub Issue: ").append(cmd.githubIssueUrl());

        slackNotificationPort.sendMessage(messageBody.toString());
    }
}
