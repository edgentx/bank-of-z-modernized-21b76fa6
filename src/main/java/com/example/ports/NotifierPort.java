package com.example.ports;

/**
 * Port interface for sending notifications (e.g., to Slack).
 * Used by the temporal workflow logic to decouple from specific implementations.
 */
public interface NotifierPort {
    
    /**
     * Sends a notification with the specified body content.
     * @param body The content of the message.
     */
    void send(String body);
}