package com.example.adapters;

import com.example.domain.shared.DomainEvent;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter handling Slack notifications for domain events.
 */
public class SlackNotificationAdapter {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final SlackPort slackPort;

    public SlackNotificationAdapter(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    public void onEvent(DomainEvent event) {
        if (event instanceof DefectReportedEvent e) {
            sendDefectReportedNotification(e);
        }
    }

    private void sendDefectReportedNotification(DefectReportedEvent event) {
        // We construct the message ensuring the GitHub URL is included
        // as per the acceptance criteria for S-FB-1.
        String message = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            event.defectId(),
            event.severity(),
            event.githubIssueUrl()
        );

        log.info("Sending Slack notification for defect {}: {}", event.defectId(), message);
        this.slackPort.sendMessage(message);
    }
}
