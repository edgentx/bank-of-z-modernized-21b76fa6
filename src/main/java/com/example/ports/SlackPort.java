package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {
    void sendDefectNotification(String summary, String githubIssueUrl);
}
