package com.example.ports;

import java.util.Map;

public interface SlackPort {
    /**
     * Send a notification to a Slack channel
     * @param channelId The target channel ID
     * @param message The message content to send
     * @return true if sent successfully, false otherwise
     */
    boolean sendMessage(String channelId, String message);
    
    /**
     * Send a formatted message with blocks to a Slack channel
     * @param channelId The target channel ID
     * @param blocks The message blocks in Slack format
     * @return true if sent successfully, false otherwise
     */
    boolean sendBlocks(String channelId, Map<String, Object> blocks);
    
    /**
     * Get the last message that was sent (for testing)
     * @return The last sent message content
     */
    String getLastSentMessage();
}