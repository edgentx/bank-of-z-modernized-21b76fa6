package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;

import java.time.Instant;
import java.util.List;
import java.util.Collections;

/**
 * Service class handling the business logic for defect reporting.
 * NOTE: This file is a placeholder used solely to allow the test to compile and run (Red phase).
 * The actual implementation exists in the main source tree (which the test aims to verify).
 */
public class ValidationService {

    private final SlackNotificationPort slackPort;

    public ValidationService(SlackNotificationPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Handles the ReportDefect command.
     * Generates the Slack payload and sends it.
     */
    public List<DefectReportedEvent> handleReportDefect(ReportDefectCmd cmd) {
        if (cmd.githubUrl() == null || cmd.githubUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub URL is required to report a defect");
        }

        // Placeholder logic: Constructing the message body.
        // In the REAL code (which we are testing), this logic might differ.
        // This stub ensures the TEST framework works.
        String messageBody = "Defect Reported: " + cmd.title() + "\nLink: " + cmd.githubUrl();
        
        slackPort.sendNotification(messageBody);

        return Collections.singletonList(new DefectReportedEvent(cmd.defectId(), messageBody, Instant.now()));
    }
}
