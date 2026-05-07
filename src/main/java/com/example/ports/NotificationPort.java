package com.example.ports;

import com.example.vforce.shared.ReportDefectCommand;

/**
 * Port interface for sending notifications (e.g., Slack).
 * Allows mocking in tests without real I/O.
 */
public interface NotificationPort {
    void notifyChannel(ReportDefectCommand command);
}