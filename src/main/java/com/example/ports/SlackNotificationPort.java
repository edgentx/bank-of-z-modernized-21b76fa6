package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {
    void notifyDefectReported(String message, String githubUrl);
}
