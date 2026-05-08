package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.SlackNotificationPort;

import java.time.Instant;
import java.util.List;

/**
 * Reconciliation aggregate responsible for handling reporting commands.
 * Implements S-FB-1 logic to format and post defect details to Slack.
 */
public class ReconciliationBatch extends AggregateRoot {

    private final String batchId;
    private final SlackNotificationPort slackNotificationPort;

    public ReconciliationBatch(String batchId, SlackNotificationPort slackNotificationPort) {
        this.batchId = batchId;
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public String id() {
        return batchId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ReportDefectCmd c) {
            return handleReportDefect(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleReportDefect(ReportDefectCmd cmd) {
        // Validation logic required by S-FB-1
        if (cmd.defectId() == null || cmd.defectId().isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null");
        }

        // Logic to format GitHub URL based on Defect ID (e.g., VW-454 -> 454)
        String issueNumber = extractIssueNumber(cmd.defectId());
        String githubUrl = "https://github.com/tech-debt/project/issues/" + issueNumber;

        // Construct Slack Body
        String messageBody = String.format(
                "Defect Reported: %s | Project: %s | Severity: %s | Details: %s",
                cmd.title(), cmd.projectId(), cmd.severity(), githubUrl
        );

        // Execute Side Effect: Send Notification
        // Using the port to ensure we don't couple to real Slack in tests
        slackNotificationPort.sendMessage("#vforce360-issues", messageBody);

        // Create Domain Event
        DefectReportedEvent event = new DefectReportedEvent(
                this.batchId,
                cmd.defectId(),
                messageBody,
                githubUrl,
                Instant.now()
        );

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    /**
     * Extracts the numeric ID from a defect ID like 'VW-454'.
     * If format is unexpected, returns the raw ID to prevent failure.
     */
    private String extractIssueNumber(String defectId) {
        if (defectId.contains("-")) {
            String[] parts = defectId.split("-");
            return parts[parts.length - 1];
        }
        return defectId;
    }
}
