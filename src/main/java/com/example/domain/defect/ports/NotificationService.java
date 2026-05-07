package com.example.domain.defect.ports;

/**
 * Port for sending notifications (e.g., to Slack).
 * Part of the Mock Adapter pattern to decouple logic from external infrastructure.
 */
public interface NotificationService {
    
    /**
     * Sends a defect report.
     * @param content The body of the notification.
     */
    void sendDefectNotification(String content);
    
    /**
     * A simplified exception for notification failures.
     */
    class NotificationException extends RuntimeException {
        public NotificationException(String message) {
            super(message);
        }
        public NotificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
