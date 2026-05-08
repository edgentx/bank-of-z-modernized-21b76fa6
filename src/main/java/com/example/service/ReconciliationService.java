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
        // Placeholder for TDD Red Phase implementation.
        // The test verifies that slackNotificationPort.sendNotification is called
        // with a body containing cmd.githubIssueUrl().
        
        // Implementation will be added in the Green phase.
        throw new UnsupportedOperationException("Implementation missing");
    }
}
