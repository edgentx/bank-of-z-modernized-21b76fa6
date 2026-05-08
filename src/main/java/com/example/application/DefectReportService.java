package com.example.application;

import com.example.config.SlackConfig;
import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Application Service handling the logic for defect reporting.
 * This acts as the bridge between the temporal workflow (or API layer)
 * and the infrastructure adapters (Slack).
 */
@Service
public class DefectReportService {

    private static final Logger LOG = Logger.getLogger(DefectReportService.class.getName());

    private final SlackNotificationPort slackNotificationPort;
    private final SlackConfig config;

    public DefectReportService(SlackNotificationPort slackNotificationPort, SlackConfig config) {
        this.slackNotificationPort = slackNotificationPort;
        this.config = config;
    }

    /**
     * Handles the ReportDefectCmd.
     * Validates the input, formats the message using the configured template,
     * and dispatches it via the port.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        if (cmd == null || cmd.githubUrl() == null || cmd.githubUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub URL is required to report a defect.");
        }

        String messageBody = formatMessage(cmd.title(), cmd.githubUrl());
        slackNotificationPort.send(messageBody);
    }

    private String formatMessage(String title, String url) {
        // Use the configured template from SlackConfig
        return config.getMessageTemplate()
                .replace("{title}", title != null ? title : "Unknown Defect")
                .replace("{url}", url);
    }
}
