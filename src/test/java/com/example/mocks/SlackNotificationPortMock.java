package com.example.mocks;

import com.example.domain.vforce360.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;

/**
 * Mock adapter for Slack Notification.
 * Implements the port and stores the last sent body for verification in tests.
 */
public class SlackNotificationPortMock implements SlackNotificationPort {
    private String lastBody;
    private String lastChannel;

    @Override
    public void notifyDefect(DefectReportedEvent event, String githubIssueUrl) {
        if (githubIssueUrl == null) {
            throw new IllegalArgumentException("GitHub URL cannot be null when notifying defect");
        }
        
        // Replicate the exact formatting expected by the defect fix
        // "Slack body includes GitHub issue: <url>"
        StringBuilder builder = new StringBuilder();
        builder.append("Defect Detected: ").append(event.summary()).append("\n");
        builder.append("Severity: ").append(event.severity()).append("\n");
        builder.append("GitHub issue: ").append(githubIssueUrl).append("\n"); // The critical line
        
        this.lastBody = builder.toString();
    }

    public String getLastBody() {
        return lastBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }
}