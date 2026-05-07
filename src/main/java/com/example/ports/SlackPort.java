package com.example.ports;

/** Port for posting Slack notifications. */
public interface SlackPort {
    /** Posts a message and returns the formatted body sent. */
    String postMessage(String channel, String text);
}
