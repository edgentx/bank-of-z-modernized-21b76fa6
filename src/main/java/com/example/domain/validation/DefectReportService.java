package com.example.domain.validation;

import com.example.domain.shared.ports.GitHubPort;
import com.example.domain.shared.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for reporting defects.
 * Orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 */
@Service
public class DefectReportService {

    private static final Logger logger = LoggerFactory.getLogger(DefectReportService.class);

    private final GitHubPort gitHubPort;
    private final NotificationPort notificationPort;

    /**
     * Constructor for dependency injection.
     *
     * @param notificationPort The port for sending Slack notifications.
     * @param gitHubPort       The port for interacting with GitHub issues.
     */
    public DefectReportService(NotificationPort notificationPort, GitHubPort gitHubPort) {
        this.notificationPort = notificationPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and sending a Slack notification.
     * <p>
     * This method implements the workflow for defect reporting:
     * 1. Create a GitHub issue using the provided title and description.
     * 2. If successful, send a Slack notification containing the GitHub issue URL.
     * 3. If GitHub issue creation fails, the operation is aborted and false is returned.
     * 4. If Slack notification fails, the method returns false, but the issue may already exist.
     *
     * @param defectId    The ID of the defect (e.g., "VW-454").
     * @param title       The title of the defect.
     * @param description The description of the defect.
     * @param channel     The Slack channel to notify.
     * @return {@code true} if the defect was reported successfully (GitHub issue created and Slack notification sent), {@code false} otherwise.
     */
    public boolean reportDefect(String defectId, String title, String description, String channel) {
        logger.info("Reporting defect {}: {}", defectId, title);

        // Step 1: Create GitHub Issue
        String githubUrl = gitHubPort.createIssue(title, description);
        if (githubUrl == null) {
            logger.error("Failed to create GitHub issue for defect {}", defectId);
            return false;
        }

        logger.info("GitHub issue created for defect {}: {}", defectId, githubUrl);

        // Step 2: Send Slack Notification
        // The defect report service ensures the URL is passed to the notification port
        boolean notificationSent = notificationPort.sendDefectReport(
            defectId,
            title,
            description,
            githubUrl,
            channel
        );

        if (!notificationSent) {
            logger.error("Failed to send Slack notification for defect {}", defectId);
            return false;
        }

        logger.info("Slack notification sent for defect {} to channel {}", defectId, channel);
        return true;
    }
}
