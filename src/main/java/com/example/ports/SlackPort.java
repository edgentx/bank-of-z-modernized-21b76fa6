package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * Adheres to the Adapter/Port pattern required by the repo.
 */
public interface SlackPort {
    
    /**
     * Sends a notification to Slack.
     * @param message The formatted message body.
     * @return The confirmation string or message ID.
     */
    String sendNotification(String message);
}
