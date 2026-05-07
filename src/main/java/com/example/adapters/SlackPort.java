package com.example.adapters;

/**
 * Port interface for posting messages to Slack.
 */
public interface SlackPort {
    void postMessage(String channel, String body);
}
