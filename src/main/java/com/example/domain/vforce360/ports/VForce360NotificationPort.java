package com.example.domain.vforce360.ports;

import com.example.domain.vforce360.model.DefectReportedEvent;

/**
 * Port for sending notifications to external systems like Slack.
 */
public interface VForce360NotificationPort {
    /**
     * Posts a defect report to the configured channel (e.g., Slack).
     *
     * @param event The domain event containing defect details.
     */
    void postDefectNotification(DefectReportedEvent event);
}
