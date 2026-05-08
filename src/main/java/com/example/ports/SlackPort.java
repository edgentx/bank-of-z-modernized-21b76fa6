package com.example.ports;

import com.example.model.DefectReport;

/**
 * Port interface for Slack notifications.
 * Used by the Temporal worker logic to alert the engineering team.
 */
public interface SlackPort {
    void sendDefectNotification(DefectReport report);
}
