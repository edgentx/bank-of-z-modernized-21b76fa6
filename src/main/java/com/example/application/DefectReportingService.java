package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service for Defect Reporting.
 * Orchestrates the domain logic and notifications (VW-454 fix).
 */
@Service
public class DefectReportingService {

    private static final Logger logger = LoggerFactory.getLogger(DefectReportingService.class);
    private static final String GITHUB_BASE_URL = "https://github.com/bank-of-z/vforce360/issues/";

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCommand.
     * 1. Executes aggregate logic.
     * 2. Formats the Slack notification including the GitHub URL (Fix for VW-454).
     * 3. Sends notification.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        logger.info("Reporting defect: {}", cmd.defectId());

        // 1. Domain Logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);

        events.forEach(event -> {
            if (event instanceof DefectReportedEvent e) {
                handleDefectReported(e);
            }
        });
    }

    private void handleDefectReported(DefectReportedEvent event) {
        // 2. Format Message (VW-454: Ensure GitHub URL is present and formatted)
        String githubUrl = GITHUB_BASE_URL + event.defectId();
        
        // Using Slack formatted message: <url|text>
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: <%s|View Details>",
            event.title(),
            githubUrl
        );

        // 3. Send Notification
        boolean success = slackNotificationPort.postMessage(messageBody);
        
        if (!success) {
            logger.error("Failed to send Slack notification for defect {}", event.defectId());
            // Depending on requirements, we might throw here or trigger a compensating action.
            // For now, we log the failure.
        } else {
            logger.info("Slack notification sent successfully for defect {}", event.defectId());
        }
    }
}
