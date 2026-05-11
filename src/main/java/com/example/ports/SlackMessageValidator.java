package com.example.ports;

import com.example.domain.shared.ReportDefectCmd;

/**
 * Port interface for validating and sending Slack messages.
 * Decouples the application logic from the Slack implementation details.
 */
public interface SlackMessageValidator {

    /**
     * Validates the Slack message body generated from the command.
     * Ensures required fields (like GitHub URLs) are present.
     *
     * @param cmd The defect command.
     * @return The final formatted message body.
     * @throws IllegalArgumentException if validation fails.
     */
    String validateAndFormat(ReportDefectCmd cmd);

    /**
     * Sends the validated message to Slack.
     *
     * @param formattedMessage The message to send.
     * @return true if sent successfully.
     */
    boolean send(String formattedMessage);
}
