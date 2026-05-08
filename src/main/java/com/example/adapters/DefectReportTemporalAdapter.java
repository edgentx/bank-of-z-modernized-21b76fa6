package com.example.adapters;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.DefectReportPort;
import com.example.ports.SlackPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapter acting as the bridge between the Domain/Temporal layer and the external Slack port.
 * This class formats the Slack message ensuring the GitHub URL is present,
 * addressing the defect VW-454.
 */
@Component
public class DefectReportTemporalAdapter implements DefectReportPort {

    private final SlackPort slackPort;

    @Autowired
    public DefectReportTemporalAdapter(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    @Override
    public void handleDefectReport(ReportDefectCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCmd cannot be null");
        }

        // Construct the message body ensuring the GitHub URL is included.
        // This logic fixes the defect where the URL was missing.
        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Defect Report: ").append(cmd.title() != null ? cmd.title() : "Unknown Title").append("\n");
        messageBody.append("ID: ").append(cmd.defectId()).append("\n");
        
        // Critical fix for VW-454: Ensure the URL is appended to the body.
        if (cmd.githubIssueUrl() != null && !cmd.githubIssueUrl().isBlank()) {
            messageBody.append("GitHub Issue: ").append(cmd.githubIssueUrl());
        } else {
            throw new IllegalArgumentException("GitHub Issue URL is mandatory for reporting");
        }

        // Delegate to the Slack port to send the notification.
        slackPort.sendNotification(messageBody.toString());
    }
}
