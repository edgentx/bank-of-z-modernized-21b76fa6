package com.example.domain.defect.port;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotifier {
    void sendNotification(String body);
}
