package com.example.ports;

import java.util.List;

/**
 * Port interface for Slack notifications.
 */
public interface SlackPort {

    /**
     * Sends a notification message to a Slack channel.
     *
     * @param channelId The target channel ID
     * @param messageBlocks The formatted message blocks (Slack Kit format)
     * @return true if sending was acknowledged, false otherwise
     */
    boolean sendMessage(String channelId, List<String> messageBlocks);
}