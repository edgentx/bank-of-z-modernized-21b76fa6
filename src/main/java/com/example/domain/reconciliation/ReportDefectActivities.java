package com.example.domain.reconciliation;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface for side-effects related to defect reporting.
 */
@ActivityInterface
public interface ReportDefectActivities {

    /**
     * Formats the defect payload for Slack.
     * This logic is separated to allow for independent testing and evolution of the format.
     *
     * @param githubUrl The GitHub issue URL.
     * @return The formatted Slack message payload.
     */
    String formatSlackPayload(String githubUrl);

    /**
     * Sends the notification to the external Slack system.
     *
     * @param payload The formatted message.
     */
    void sendNotification(String payload);
}