package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service handling defect reporting logic for the Validation domain.
 * S-FB-1: Implements defect reporting logic to ensure Slack body contains GitHub URL.
 */
@Service
public class ValidationService {

    private final SlackNotificationPort slackNotificationPort;

    public ValidationService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the external monitoring system (Slack).
     * Formats the message to strictly include the GitHub URL.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(DefectReportedCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("DefectReportedCommand cannot be null");
        }
        if (cmd.githubUrl() == null || cmd.githubUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub URL is required for defect reporting");
        }

        // Construct the message body. 
        // Format: "Defect reported: {defectId}\nLink: <url>"
        // Using Slack link formatting <url|optional_text> or just <url>
        String messageBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nProject: %s\nGitHub Issue: <%s>",
            cmd.defectId(),
            cmd.severity(),
            cmd.projectId(),
            cmd.githubUrl()
        );

        this.slackNotificationPort.send(messageBody);
    }
}