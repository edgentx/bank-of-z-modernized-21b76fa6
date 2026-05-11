package com.example.ports;

import com.example.domain.vforce360.model.DefectReportedEvent;

/**
 * Port for sending notifications to Slack.
 * Follows the Hexagonal Architecture pattern.
 */
public interface SlackPort {
    void sendNotification(DefectReportedEvent event);
    String formatMessageBody(DefectReportedEvent event);
}