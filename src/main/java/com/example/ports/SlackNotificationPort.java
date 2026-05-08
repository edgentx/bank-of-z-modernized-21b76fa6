package com.example.ports;

/**
 * Port for Slack notifications.
 * Used to verify defect fixes regarding URL formatting.
 */
public interface SlackNotificationPort {
    
    /**
     * Represents the outcome of a notification send.
     */
    class SendResult {
        private final boolean success;
        private final String messageBody;

        public SendResult(boolean success, String messageBody) {
            this.success = success;
            this.messageBody = messageBody;
        }

        public boolean isSuccess() { return success; }
        public String getMessageBody() { return messageBody; }
    }

    /**
     * Sends a defect report to Slack.
     * @param defectId The ID of the defect (e.g., VW-454)
     * @param description The description of the defect
     * @return SendResult containing status and the generated body
     */
    SendResult reportDefect(String defectId, String description);
}
