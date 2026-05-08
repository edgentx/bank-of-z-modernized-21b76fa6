package com.example.mocks;

import com.example.domain.validation.model.DefectReportedEvent;

/**
 * Mock interface for Slack notifications to be used in tests and proxies.
 * Mimics a Port structure.
 */
public interface MockSlackNotificationPort {
    void sendNotification(DefectReportedEvent event);
}
