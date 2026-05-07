package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;

import java.time.Instant;
import java.util.List;
import java.util.Collections;

/**
 * Service class handling the business logic for defect reporting.
 * This is the implementation that satisfies the Vw454ValidationE2ETest.
 */
public class ValidationService {

    private final SlackNotificationPort slackPort;

    /**
     * Constructor-based dependency injection.
     * @param slackPort The port (interface) for Slack operations.
     */
    public ValidationService(SlackNotificationPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Handles the ReportDefect command.
     * Generates the Slack payload, sends it via the adapter, and returns the domain event.
     *
     * @param cmd The command containing defect details.
     * @return A list containing the DefectReportedEvent.
     * @throws IllegalArgumentException if the GitHub URL is missing or blank.
     */
    public List<DefectReportedEvent> handleReportDefect(ReportDefectCmd cmd) {
        // 1. Validate Input
        if (cmd.githubUrl() == null || cmd.githubUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub URL is required to report a defect");
        }

        // 2. Construct Message Body for Slack
        // Using a simple format that includes the URL as per Acceptance Criteria.
        String messageBody = "Defect Reported: " + cmd.title() + "\nLink: " + cmd.githubUrl();
        
        // 3. Send via Port (Adapter Pattern)
        slackPort.sendNotification(messageBody);

        // 4. Emit Domain Event
        DefectReportedEvent event = new DefectReportedEvent(cmd.defectId(), messageBody, Instant.now());
        return Collections.singletonList(event);
    }
}
