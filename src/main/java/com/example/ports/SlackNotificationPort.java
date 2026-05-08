package com.example.ports;

/**
 * Port interface for Slack notifications.
 * This isolates the core logic from the actual Slack WebClient.
 */
public interface SlackNotificationPort {
    
    /**
     * Result of a notification attempt.
     */
    class NotificationResult {
        private final boolean success;
        private final String messageBody;

        public NotificationResult(boolean success, String messageBody) {
            this.success = success;
            this.messageBody = messageBody;
        }

        public boolean isSuccess() { return success; }
        public String getMessageBody() { return messageBody; }
    }

    /**
     * Sends a defect report to Slack.
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param title The title of the defect.
     * @param defectId The unique ID of the defect (VW-454).
     * @return NotificationResult containing the status and the formatted body sent.
     */
    NotificationResult publishDefect(String channel, String title, String defectId);
}
