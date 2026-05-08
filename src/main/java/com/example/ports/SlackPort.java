package com.example.ports;

/**
 * Port interface for posting notifications to Slack.
 * Used by defect reporting workflows.
 */
public interface SlackPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The message body content
     */
    void postMessage(String channel, String body);
}
