package com.example.ports;

/**
 * Port interface for VForce360 PM diagnostic conversation integrations.
 * Used to verify external side-effects like Slack notifications.
 */
public interface VForce360IntegrationPort {

    /**
     * Checks the target Slack channel for the last message body.
     *
     * @param channelName The name of the channel (e.g., "vforce360-issues").
     * @return The content of the last message posted in the channel.
     */
    String getLastSlackMessageBody(String channelName);

    /**
     * Verifies if a defect report was executed via the temporal worker.
     * (Assumed trigger for the Slack message).
     */
    boolean wasDefectReportExecuted(String defectId);
}