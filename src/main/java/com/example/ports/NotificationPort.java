package com.example.ports;

import com.example.domain.defect.model.DefectReportedEvent;

/**
 * Port for sending notifications (e.g., Slack).
 * Abstracted to allow mocking in tests and real implementation in production.
 */
public interface NotificationPort {
    void sendDefectAlert(DefectReportedEvent event);
}
