package com.example.domain.validation.service;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.commands.ReportDefectCmd;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Application Service for the Validation Aggregate.
 * Handles commands and orchestrates side effects (like Slack notifications).
 */
@Service
public class ValidationService {

    private final ValidationRepository repository;
    private final SlackNotificationPort slackNotificationPort;

    public ValidationService(ValidationRepository repository, SlackNotificationPort slackNotificationPort) {
        this.repository = repository;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefect command.
     * 1. Loads or creates the aggregate.
     * 2. Executes the command.
     * 3. Persists the aggregate.
     * 4. Notifies via Slack if an event was raised.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Load or create aggregate
        ValidationAggregate aggregate = repository.findById(cmd.validationId())
                .orElse(new ValidationAggregate(cmd.validationId()));

        // 2. Execute command
        var events = aggregate.execute(cmd);

        // 3. Persist
        repository.save(aggregate);

        // 4. Handle Side Effects (The Fix for VW-454)
        events.forEach(event -> {
            if (event instanceof com.example.domain.validation.model.DefectReportedEvent e) {
                // Construct the payload ensuring the GitHub URL is present
                Map<String, String> payload = Map.of(
                    "text", "Defect Reported: " + e.summary(),
                    "defectId", e.defectId(),
                    "githubUrl", e.githubIssueUrl() != null ? e.githubIssueUrl() : "N/A"
                );
                slackNotificationPort.sendNotification(payload);
            }
        });
    }
}
