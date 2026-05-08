package com.example.ports;

import com.example.domain.shared.DefectReportedEvent;

/**
 * Port for sending notifications (e.g., Slack, Email).
 * Defines the contract that the application layer uses to dispatch messages.
 */
public interface NotificationPort {

    /**
     * Publishes a defect report to the configured external system (Slack).
     * Implementations must ensure the GitHub URL is formatted correctly in the body.
     *
     * @param event The domain event containing defect details and links.
     */
    void publishDefectReport(DefectReportedEvent event);
}
