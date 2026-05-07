package com.example.domain.reconciliation;

import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.ports.NotificationPort;
import com.example.ports.TemporalDefectPort;

/**
 * Service to handle the reporting of defects.
 * Currently a placeholder to allow compilation and testing of the flow.
 * In a real implementation, this might be a Temporal Workflow or Activity implementation.
 */
public class DefectReportService {

    private final NotificationPort notificationPort;
    private final TemporalDefectPort temporalDefectPort;

    public DefectReportService(NotificationPort notificationPort, TemporalDefectPort temporalDefectPort) {
        this.notificationPort = notificationPort;
        this.temporalDefectPort = temporalDefectPort;
    }

    public void handleDefect(ReportDefectCmd cmd) {
        // 1. Trigger Temporal workflow (simulated)
        temporalDefectPort.reportDefect(cmd);

        // 2. Notify Slack (simulated end state of the workflow)
        // The requirement is that the body includes the GitHub URL.
        String body = String.format("Defect reported: %s. GitHub Issue: <%s>", cmd.summary(), cmd.gitHubIssueUrl());
        notificationPort.sendNotification("#vforce360-issues", body);
    }
}
