package com.example.ports;

import java.util.Map;

/**
 * Port interface for Slack integration.
 * Acts as a boundary for the external Slack API.
 */
public interface SlackClient {
    
    /**
     * Sends a message payload to a configured Slack channel.
     * 
     * @param payload Map containing the message structure (e.g., text, blocks)
     * @throws RuntimeException if the API call fails
     */
    void sendMessage(Map<String, Object> payload);
}
