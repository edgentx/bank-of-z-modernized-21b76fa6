package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used by the Temporal Workflow to send alerts without depending directly on the SDK.
 */
public interface SlackPort {
    /**
     * Posts a message to the configured Slack channel.
     *
     * @param text The body text of the message.
     * @return true if the API call was accepted, false otherwise.
     */
    boolean postMessage(String text);
}
