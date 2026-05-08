package com.example.ports;

import com.example.domain.report_defect.model.ReportDefectCommand;

/**
 * Port for sending notifications to Slack.
 * Used by the temporal-worker to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a defect report to the configured Slack channel.
     *
     * @param command the command containing the defect details
     * @return the formatted message body that was (or would be) sent
     */
    String sendDefectNotification(ReportDefectCommand command);
}
