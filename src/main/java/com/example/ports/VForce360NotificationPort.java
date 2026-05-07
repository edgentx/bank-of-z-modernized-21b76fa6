package com.example.ports;

import com.example.domain.vforce360.model.DefectReportedEvent;

/**
 * Port for sending notifications to external systems like Slack.
 * This isolates the domain from the concrete implementation of Slack/Temporal APIs.
 */
public interface VForce360NotificationPort {
    /**
     * Publishes the defect event to the notification channel (e.g., via Temporal _report_defect).
     * Implementations must format the Slack body to include the GitHub URL.
     */
    void publishDefect(DefectReportedEvent event);
}
