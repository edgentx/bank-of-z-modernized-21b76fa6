package com.example.domain.reconciliation;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activity implementation for defect reporting.
 * This class contains the actual business logic for formatting and the adapter call.
 */
public class ReportDefectActivitiesImpl implements ReportDefectActivities {

    private static final Logger logger = LoggerFactory.getLogger(ReportDefectActivitiesImpl.class);

    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectActivitiesImpl(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public String formatSlackPayload(String githubUrl) {
        // VW-454: Ensure the GitHub URL is present in the body
        return String.format("Defect reported. GitHub issue: %s", githubUrl);
    }

    @Override
    public void sendNotification(String payload) {
        try {
            slackNotificationPort.sendNotification(payload);
        } catch (Exception e) {
            logger.error("Failed to send Slack notification", e);
            throw e;
        }
    }
}