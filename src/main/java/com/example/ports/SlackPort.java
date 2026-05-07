package com.example.ports;

import java.util.Map;

/**
 * Interface for Slack notification operations.
 * Used by the Validation workflow to alert engineers.
 */
public interface SlackPort {

    /**
     * Sends a notification message to a Slack channel.
     * @param context Map containing 'body', 'channel', and optional 'projectId'.
     */
    void sendMessage(Map<String, Object> context);
}
