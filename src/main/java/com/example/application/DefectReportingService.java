package com.example.application;

import com.example.domain.reporting.model.DefectAggregate;
import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.ports.DefectRepositoryPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service orchestrating the defect reporting workflow.
 * This handles the 'Temporal-worker exec' simulation logic.
 */
@Service
public class DefectReportingService {

    private final DefectRepositoryPort repository;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(DefectRepositoryPort repository, SlackNotificationPort slackNotificationPort) {
        this.repository = repository;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handle the report_defect command.
     * 1. Execute Aggregate logic (Validation + State Change)
     * 2. Persist event
     * 3. Trigger Notification (Slack) including GitHub URL
     */
    public void reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);

        if (!events.isEmpty()) {
            // Repository call (Temporal or DB)
            repository.recordDefect(cmd.defectId(), cmd);

            // Slack Notification Call
            var event = events.get(0);
            String slackBody = formatSlackBody(event.githubUrl(), cmd.severity());
            slackNotificationPort.postMessage("#vforce360-issues", slackBody);
        }
    }

    private String formatSlackBody(String url, String severity) {
        return String.format(
            "Defect Reported (Severity: %s)\nGitHub Issue: %s",
            severity != null ? severity : "LOW",
            url
        );
    }
}
