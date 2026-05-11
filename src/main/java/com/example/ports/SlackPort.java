package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {
    /**
     * Posts a message body to a Slack channel.
     * @param body The text content to post.
     */
    void postMessage(String body);
}
