package com.example.ports;

import com.example.domain.validation.DefectReportedEvent;

/**
 * Port for sending notifications to external messaging services (e.g., Slack).
 * This abstraction allows the domain to trigger notifications without depending on concrete implementations.
 */
public interface SlackNotificationPort {
    void notifyDefectReported(DefectReportedEvent event);
}
