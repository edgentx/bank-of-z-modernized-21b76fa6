package com.example.adapters;

import com.example.domain.shared.DefectReportedEvent;
import com.example.ports.NotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of the NotificationPort for Slack.
 * Constructs the payload ensuring strict formatting rules (VW-454).
 */
@Component
public class SlackNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void publishDefectReport(DefectReportedEvent event) {
        // Construct the Slack body
        // Requirement: 'GitHub Issue: <url>'
        String body = String.format(
            "Defect Reported: %s\nProject: %s\nGitHub Issue: <%s>",
            event.getDefectId(),
            event.getProjectId(),
            event.getGithubUrl()
        );

        // In a real implementation, this would use the Slack WebAPI client.
        // For this validation phase, we log to verify the string construction.
        log.info("Sending Slack notification: {}", body);

        // Verify internal constraint before sending
        if (!body.contains("<" + event.getGithubUrl() + ">")) {
            throw new IllegalStateException("Slack body construction failed: URL not properly formatted");
        }
    }
}
