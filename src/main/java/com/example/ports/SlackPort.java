package com.example.ports;

/**
 * Port for posting messages to Slack.
 * This is the interface that the production code will implement
 * to talk to the real Slack API, and which tests will mock.
 */
public interface SlackPort {
    /**
     * Posts a message body to the VForce360 issues channel.
     * @return boolean indicating success.
     */
    boolean postMessage(String text);
}
