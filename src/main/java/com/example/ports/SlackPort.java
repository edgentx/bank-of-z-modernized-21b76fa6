package com.example.ports;

/**
 * Port interface for Slack notification services.
 * External dependency (Slack API) abstracted via this port.
 */
public interface SlackPort {
    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The channel ID or name (e.g., #vforce360-issues).
     * @param message The formatted message body.
     */
    void postMessage(String channel, String message);
}
